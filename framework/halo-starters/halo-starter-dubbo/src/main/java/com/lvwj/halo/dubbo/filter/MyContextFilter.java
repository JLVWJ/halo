package com.lvwj.halo.dubbo.filter;

import com.lvwj.halo.dubbo.util.RpcContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo过滤器: 自定义上下文处理
 *
 * @author lvweijie
 * @date 2024年04月01日 10:07
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER})
public class MyContextFilter implements Filter, Filter.Listener {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContextUtil.setLocaleContextHolder();
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        RpcContextUtil.clearLocaleContextHolder();
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {
        RpcContextUtil.clearLocaleContextHolder();
    }
}
