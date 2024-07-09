package com.lvwj.halo.statemachine.builder;


import com.lvwj.halo.common.models.BusinessScenario;
import com.lvwj.halo.statemachine.Action;
import com.lvwj.halo.statemachine.Condition;
import com.lvwj.halo.statemachine.State;
import com.lvwj.halo.statemachine.Transition;
import com.lvwj.halo.statemachine.impl.StateHelper;
import com.lvwj.halo.statemachine.impl.TransitionType;

import java.util.Map;

/**
 * TransitionBuilderImpl
 *
 * @author Frank Zhang
 * @date 2020-02-07 10:20 PM
 */
class TransitionBuilderImpl<S,E,C> extends AbstractTransitionBuilder<S,E,C> implements ExternalTransitionBuilder<S,E,C>, InternalTransitionBuilder<S,E,C> {


    private State<S, E, C> source;
    private Transition<S, E, C> transition;

    public TransitionBuilderImpl(Map<S, State<S, E, C>> stateMap, TransitionType transitionType) {
        super(stateMap, transitionType);
    }

    /**
     * 添加业务场景
     *
     * @param scenario 业务场景
     * @author lvweijie
     * @date 2023/11/18 10:32
     */
    @Override
    public Scenario<S, E, C> scenario(BusinessScenario scenario) {
        this.scenario = scenario;
        return this;
    }

    @Override
    public From<S, E, C> from(S... stateIds) {
        source = StateHelper.getState(stateMap, stateIds[0]);
        return this;
    }

    @Override
    public To<S, E, C> within(S stateId) {
        source = target = StateHelper.getState(stateMap, stateId);
        return this;
    }

    @Override
    public On<S, E, C> on(E event) {
        transition = source.addTransition(event, target, transitionType, scenario);
        return this;
    }

    @Override
    public When<S, E, C> when(Condition<C> condition) {
        transition.setCondition(condition);
        return this;
    }

    @Override
    public void perform(Action<S, E, C> action) {
        transition.setAction(action);
    }
}
