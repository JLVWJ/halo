package com.lvwj.halo.statemachine;

import com.lvwj.halo.common.models.BusinessScenario;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * @author lvweijie
 * @date 2023年11月17日 19:49
 */
@AllArgsConstructor(staticName = "of")
public class StateTransition<S, E> {

    private S[] from;

    private S to;

    private E event;

    private BusinessScenario scenario;

    public static <S, E> StateTransition<S, E> of(S[] from, S to, E event) {
        return StateTransition.of(from, to, event, BusinessScenario.of());
    }

    public S[] from() {
        return this.from;
    }

    public S to() {
        return this.to;
    }

    public E event() {
        return this.event;
    }

    public BusinessScenario scenario() {
        return this.scenario;
    }

    @Override
    public String toString() {
        return String.format("From[%s] To[%s] On[%s] Scenario[%s]", Arrays.toString(from), to, event, scenario);
    }
}
