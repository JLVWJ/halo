package com.lvwj.halo.statemachine.builder;


import com.lvwj.halo.common.models.BusinessScenario;

/**
 * ExternalTransitionBuilder
 *
 * @author Frank Zhang
 * @date 2020-02-07 6:11 PM
 */
public interface ExternalTransitionBuilder<S, E, C> {
    Scenario<S, E, C> scenario(BusinessScenario scenario);

}
