package com.lvwj.halo.statemachine.builder;


import com.lvwj.halo.statemachine.State;
import com.lvwj.halo.statemachine.StateMachine;
import com.lvwj.halo.statemachine.StateMachineFactory;
import com.lvwj.halo.statemachine.impl.StateMachineImpl;
import com.lvwj.halo.statemachine.impl.TransitionType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StateMachineBuilderImpl
 *
 * @author Frank Zhang
 * @date 2020-02-07 9:40 PM
 */
public class StateMachineBuilderImpl<S, E, C> implements StateMachineBuilder<S, E, C> {

    /**
     * StateMap is the same with stateMachine, as the core of state machine is holding reference to states.
     */
    private final Map<S, State<S, E, C>> stateMap = new ConcurrentHashMap<>();
    private final StateMachineImpl<S, E, C> stateMachine = new StateMachineImpl<>(stateMap);
    private FailCallback<S, E, C> failCallback = new NumbFailCallback<>();

    @Override
    public ExternalTransitionBuilder<S, E, C> externalTransition() {
        return new TransitionBuilderImpl<>(stateMap, TransitionType.EXTERNAL);
    }

    @Override
    public ExternalTransitionsBuilder<S, E, C> externalTransitions() {
        return new TransitionsBuilderImpl<>(stateMap, TransitionType.EXTERNAL);
    }

    @Override
    public InternalTransitionBuilder<S, E, C> internalTransition() {
        return new TransitionBuilderImpl<>(stateMap, TransitionType.INTERNAL);
    }

    @Override
    public void setFailCallback(FailCallback<S, E, C> callback) {
        this.failCallback = callback;
    }

    @Override
    public StateMachine<S, E, C> build(String machineId) {
        stateMachine.setMachineId(machineId);
        stateMachine.setReady(true);
        stateMachine.setFailCallback(failCallback);
        StateMachineFactory.register(stateMachine);
        return stateMachine;
    }

}
