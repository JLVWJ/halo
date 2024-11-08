package com.lvwj.halo.statemachine.builder;

/**
 * Default fail callback, do nothing.
 *
 * @author 龙也
 * @date 2022/9/15 12:02 PM
 */
public class NumbFailCallback<S, E, C> implements FailCallback<S, E, C> {

    @Override
    public void onFail(S sourceState, E event, C context) {
        //do nothing
    }
}
