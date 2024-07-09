package com.lvwj.halo.statemachine;

/**
 * Generic strategy interface used by a state machine to respond
 * events by executing an {@code Action}.
 *
 * @author Frank Zhang
 * @date 2020-02-07 2:51 PM
 */
public interface Action<S, E, C> {

    void execute(S from, S to, E event, C context);

}
