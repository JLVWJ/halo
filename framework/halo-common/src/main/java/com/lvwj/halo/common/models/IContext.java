package com.lvwj.halo.common.models;

/**
 *  上下文基接口
 *
 * @author lvweijie
 * @date 2023年11月17日 17:56
 */
public interface IContext {

    /**
     * 业务场景
     *
     * @author lvweijie
     * @date 2023/11/17 17:55
     */
    default BusinessScenario scenario() {
        return BusinessScenario.of();
    }

    /**
     * 找不到对应的业务场景是否降级
     *
     * @author lvweijie
     * @date 2023/11/17 17:55
     */
    default boolean failover() {
        return false;
    }
}
