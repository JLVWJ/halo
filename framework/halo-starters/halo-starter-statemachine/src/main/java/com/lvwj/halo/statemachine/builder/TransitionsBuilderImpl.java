package com.lvwj.halo.statemachine.builder;

import com.lvwj.halo.common.models.BusinessScenario;
import com.lvwj.halo.statemachine.Action;
import com.lvwj.halo.statemachine.Condition;
import com.lvwj.halo.statemachine.State;
import com.lvwj.halo.statemachine.Transition;
import com.lvwj.halo.statemachine.impl.StateHelper;
import com.lvwj.halo.statemachine.impl.TransitionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TransitionsBuilderImpl
 *
 * @author Frank Zhang
 * @date 2020-02-08 7:43 PM
 */
public class TransitionsBuilderImpl<S,E,C> extends AbstractTransitionBuilder<S,E,C> implements ExternalTransitionsBuilder<S,E,C> {
    /**
     * This is for fromAmong where multiple sources can be configured to point to one target
     */
    private List<State<S, E, C>> sources = new ArrayList<>();

    private List<Transition<S, E, C>> transitions = new ArrayList<>();

    public TransitionsBuilderImpl(Map<S, State<S, E, C>> stateMap, TransitionType transitionType) {
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
        for (S stateId : stateIds) {
            sources.add(StateHelper.getState(super.stateMap, stateId));
        }
        return this;
    }

    @Override
    public On<S, E, C> on(E event) {
        for (State source : sources) {
            Transition transition = source.addTransition(event, target, transitionType, scenario);
            transitions.add(transition);
        }
        return this;
    }

    @Override
    public When<S, E, C> when(Condition<C> condition) {
        for (Transition transition : transitions) {
            transition.setCondition(condition);
        }
        return this;
    }

    @Override
    public void perform(Action<S, E, C> action) {
        for (Transition transition : transitions) {
            transition.setAction(action);
        }
    }
}
