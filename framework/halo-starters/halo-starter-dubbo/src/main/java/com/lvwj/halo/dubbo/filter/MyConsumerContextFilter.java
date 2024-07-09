package com.lvwj.halo.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

/**
 * @author lvweijie
 * @date 2024年04月23日 11:19
 */
@Activate(group = CONSUMER, order = -9999)
public class MyConsumerContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        return invoker.invoke(invocation);
    }
}
