package com.lvwj.halo.dubbo.filter;

import com.lvwj.halo.dubbo.util.RpcContextUtil;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * @author lvweijie
 * @date 2024年04月23日 11:19
 */
@Activate(group = CONSUMER, order = -9999)
public class MyConsumerContextFilter implements Filter {

    @Trace
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContextUtil.setTraceId(RpcContextUtil.getTraceId());
        return invoker.invoke(invocation);
    }
}
