package com.lvwj.halo.swagger2.core.web;

import com.lvwj.halo.swagger2.config.properties.Swagger2Properties;
import com.lvwj.halo.swagger2.core.toolkit.HttpMatch;
import com.lvwj.halo.swagger2.core.toolkit.NameDiscover;
import com.lvwj.halo.swagger2.core.toolkit.ReferenceManager;
import io.swagger.annotations.Api;
import io.swagger.util.Json;
import io.swagger.util.PrimitiveType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map.Entry;

@Slf4j
@Controller
@RequestMapping("${halo.swagger2.dubbo.http:h}")
@Api(hidden = true)
public class DubboHttpController {

    private static final String CLUSTER_RPC = "rpc";

    @Autowired
    private ReferenceManager referenceManager;

    @Autowired
    private Swagger2Properties swagger2Properties;

    @RequestMapping(value = "/{interfaceClass}/{methodName}", produces = {"application/json; charset=utf-8", "application/x-www-form-urlencoded"})
    @ResponseBody
    public ResponseEntity<String> invokeDubbo(@PathVariable("interfaceClass") String interfaceClass,
                                              @PathVariable("methodName") String methodName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return invokeDubbo(interfaceClass, methodName, null, request, response);
    }

    @RequestMapping(value = "/{interfaceClass}/{methodName}/{operationId}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<String> invokeDubbo(@PathVariable("interfaceClass") String interfaceClass,
                                              @PathVariable("methodName") String methodName,
                                              @PathVariable("operationId") String operationId,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!swagger2Properties.getDubbo().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Object ref;
        Method method = null;
        Object result;

        Entry<Class<?>, Object> entry = referenceManager.getRef(interfaceClass);

        if (null == entry) {
            log.info("No Ref Service FOUND.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ref = entry.getValue();
        HttpMatch httpMatch = new HttpMatch(entry.getKey(), AopUtils.getTargetClass(ref));
        Method[] interfaceMethods = httpMatch.findInterfaceMethods(methodName);

        if (null != interfaceMethods && interfaceMethods.length > 0) {
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, operationId, request.getMethod());
            method = httpMatch.matchRefMethod(refMethods, methodName, request.getParameterMap().keySet());
        }
        if (null == method) {
            log.info("No Service Method FOUND.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String[] parameterNames = NameDiscover.parameterNameDiscover.getParameterNames(method);

        String cluster = swagger2Properties.getDubbo().getCluster();
        log.info("[Swagger-dubbo] Invoke by " + cluster);
        if (CLUSTER_RPC.equals(cluster)) {
            ref = referenceManager.getProxy(interfaceClass);
            if (null == ref) {
                log.info("No Ref Proxy Service FOUND.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            method = ref.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (null == method) {
                log.info("No Proxy Service Method FOUND.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        log.debug("[Swagger-dubbo] Invoke dubbo service method:{},parameter:{}", method, Json.pretty(request.getParameterMap()));
        if (null == parameterNames || parameterNames.length == 0) {
            result = method.invoke(ref);
        } else {
            Object[] args = new Object[parameterNames.length];
            Type[] parameterTypes = method.getGenericParameterTypes();
            Class<?>[] parameterClazz = method.getParameterTypes();

            for (int i = 0; i < parameterNames.length; i++) {
                Object suggestParameterValue = suggestParameterValue(parameterTypes[i],
                        parameterClazz[i],
                        request.getParameter(parameterNames[i]));
                args[i] = suggestParameterValue;
            }
            if (AopUtils.isAopProxy(ref)) {
                method = ref.getClass().getMethod(methodName, parameterClazz);
            }
            result = method.invoke(ref, args);
        }
        return ResponseEntity.ok(Json.mapper().writeValueAsString(result));
    }

    private Object suggestParameterValue(Type type, Class<?> cls, String parameter)  {
        PrimitiveType fromType = PrimitiveType.fromType(type);
        if (null != fromType) {
            DefaultConversionService service = new DefaultConversionService();
            boolean actual = service.canConvert(String.class, cls);
            if (actual) {
                return service.convert(parameter, cls);
            }
        } else {
            if (null == parameter)
                return null;
            try {
                return Json.mapper().readValue(parameter, cls);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "The parameter value [" + parameter + "] should be json of [" + cls.getName() + "] Type.", e);
            }
        }
        try {
            return Class.forName(cls.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
