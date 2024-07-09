package com.lvwj.halo.statemachine.impl;


import com.lvwj.halo.statemachine.Transition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * EventTransitions
 *
 * 同一个Event可以触发多个Transitions，https://github.com/alibaba/COLA/pull/158
 *
 * @author Frank Zhang
 * @date 2021-05-28 5:17 PM
 */
public class EventTransitions<S,E,C> {
    private final HashMap<E, List<Transition<S, E, C>>> eventTransitions;

    public EventTransitions() {
        eventTransitions = new HashMap<>();
    }

    public void put(E event, Transition<S, E, C> transition) {
        List<Transition<S, E, C>> transitions;
        if (eventTransitions.get(event) == null) {
            transitions = new ArrayList<>();
            transitions.add(transition);
            eventTransitions.put(event, transitions);
        } else {
            transitions = eventTransitions.get(event);
            verify(transitions, transition);
            transitions.add(transition);
        }
        //按业务场景排序
        transitions.sort(Comparator.comparing(s -> s.getScenario().order()));
    }

    /**
     * Per one source and target state, there is only one transition is allowed
     *
     * @param existingTransitions
     * @param newTransition
     */
    private void verify(List<Transition<S, E, C>> existingTransitions, Transition<S, E, C> newTransition) {
        for (Transition<S, E, C> transition : existingTransitions) {
            if (transition.equals(newTransition)) {
                throw new StateMachineException(transition + " already Exist, you can not add another one");
            }
        }
    }

    public List<Transition<S, E, C>> get(E event) {
        return eventTransitions.get(event);
    }

    public List<Transition<S, E, C>> allTransitions() {
        List<Transition<S, E, C>> allTransitions = new ArrayList<>();
        for (List<Transition<S, E, C>> transitions : eventTransitions.values()) {
            allTransitions.addAll(transitions);
        }
        return allTransitions;
    }
}
