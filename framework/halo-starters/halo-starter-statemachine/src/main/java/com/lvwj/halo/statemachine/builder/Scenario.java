package com.lvwj.halo.statemachine.builder;

/**
 * Scenario
 *
 * @author Frank Zhang
 * @date 2020-02-07 6:14 PM
 */
public interface Scenario<S, E, C> extends From<S, E, C>{
    From<S, E, C> from(S... stateIds);
}
