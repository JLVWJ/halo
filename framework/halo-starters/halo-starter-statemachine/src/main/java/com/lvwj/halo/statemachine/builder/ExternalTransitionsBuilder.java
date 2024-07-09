package com.lvwj.halo.statemachine.builder;


import com.lvwj.halo.common.models.BusinessScenario;

/**
 * ExternalTransitionsBuilder
 *
 * This builder is for multiple transitions, currently only support multiple sources <----> one target
 *
 * @author Frank Zhang
 * @date 2020-02-08 7:41 PM
 */
public interface ExternalTransitionsBuilder<S, E, C> {
    Scenario<S, E, C> scenario(BusinessScenario scenario);
}
