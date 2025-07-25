package com.lvwj.halo.dubbo.filter;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.dto.response.R;
import com.lvwj.halo.common.enums.IErrorEnum;
import com.lvwj.halo.common.exceptions.BusinessException;
import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.core.i18n.I18nUtil;
import com.lvwj.halo.dubbo.config.prop.HaloDubboProperties;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dubbo全局异常处理
 *
 * @author lvweijie
 * @date 2023年11月16日 20:02
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER})
public class MyExceptionFilter implements Filter, Filter.Listener {

    private static final HaloDubboProperties haloDubboProperties = SpringUtil.getBean(HaloDubboProperties.class);

    @Trace
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        StopWatch sw = new StopWatch();
        String methodName = getMethodName(invoker, invocation);
        try {
            //打印日志
            printRequestLog(methodName, invocation.getArguments());
            sw.start();
            return invoker.invoke(invocation);
        } finally {
            sw.stop();
            log.info("{}请求耗时(毫秒): {}", methodName, sw.getTotalTimeMillis());
        }
    }

    @Trace
    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        //打印日志
        printResponseLog(getMethodName(invoker, invocation), appResponse.getValue(), appResponse.getException());
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            Throwable throwable = Exceptions.unwrap(appResponse.getException());
            if (throwable instanceof ConstraintViolationException cve) {
                String message = "参数验证异常:[" + cve.getConstraintViolations().stream().map(s -> s.getPropertyPath().toString() + StringPool.COLON + s.getMessage()).collect(Collectors.joining(StringPool.SEMICOLON)) + "]";
                appResponse.setValue(R.fail(message));
                appResponse.setException(null);
                return;
            }
            if (throwable instanceof BusinessException be) {
                R<Object> dto;
                boolean displayable = Optional.ofNullable(be.getErrorEnum()).map(IErrorEnum::getDisplayable).orElse(true);
                if (displayable) {
                    //国际化处理
                    be.setMessage(I18nUtil.getMessage(be.getCode() + "", be.getArgs(), be.getMessage()));
                    dto = R.fail(be);
                } else {
                    dto = R.fail();
                }
                appResponse.setValue(dto);
                appResponse.setException(null);
                return;
            }
            if (throwable instanceof Exception) {
                appResponse.setValue(R.fail());
                appResponse.setException(null);
            }
        }
    }

    @Trace
    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        //打印日志
        printErrorLog(getMethodName(invoker, invocation), t);
    }

    private String getMethodName(Invoker<?> invoker, Invocation invocation) {
        String methodName = RpcUtils.getMethodName(invocation);
        return invoker.getInterface().getSimpleName() + "." + methodName;
    }

    private void printRequestLog(String methodName, Object[] args) {
        String requestParam = JsonUtil.toJson(args);
        if (enablePrintRequestLog(methodName)) {
            log.info("{}请求入参:{}", methodName, requestParam);
        }
        ActiveSpan.tag("requestParams", requestParam);
        //Sentry.getCurrentHub().addBreadcrumb("requestParams",requestParam);
    }

    private void printResponseLog(String methodName, Object result, Throwable t) {
        if (!ObjectUtils.isEmpty(result)) {
            String res = JsonUtil.toJson(result);
            if (enablePrintResponseLog(methodName)) {
                log.info("{}请求结果:{}", methodName, res);
                ActiveSpan.tag("result", res);
            }
        } else if (null != t) {
            if (t instanceof BusinessException be) {
                log.error("{}请求异常:", methodName, be);
                ActiveSpan.error(be.getCode() + ":" + be.getMessage());
            } else if (t instanceof ConstraintViolationException cve) {
                String message = "[" + cve.getConstraintViolations().stream().map(s -> s.getPropertyPath().toString() + StringPool.COLON + s.getMessage()).collect(Collectors.joining(StringPool.SEMICOLON)) + "]";
                log.error("{}请求参数验证异常:{}", methodName, message, t);
                ActiveSpan.error(t);
            } else {
                printErrorLog(methodName, t);
            }
        }
    }

    private void printErrorLog(String methodName, Throwable throwable) {
        log.error("{}请求异常:", methodName, throwable);
        ActiveSpan.error(throwable);
    }

    private boolean enablePrintRequestLog(String methodName) {
        if (!haloDubboProperties.getFilter().getRequestLog().getEnable())
            return false;

        List<String> excludeFacadeMethods = haloDubboProperties.getFilter().getRequestLog().getExcludeMethods();
        return !excludeFacadeMethods.contains(methodName);
    }

    private boolean enablePrintResponseLog(String methodName) {
        if (!haloDubboProperties.getFilter().getResponseLog().getEnable())
            return false;

        List<String> excludeFacadeMethods = haloDubboProperties.getFilter().getResponseLog().getExcludeMethods();
        return !excludeFacadeMethods.contains(methodName);
    }
}

