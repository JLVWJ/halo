package com.lvwj.halo.statemachine;

import cn.hutool.extra.spring.SpringUtil;
import com.lvwj.halo.common.models.BusinessScenario;
import com.lvwj.halo.common.models.IContext;
import com.lvwj.halo.statemachine.builder.AlertFailCallback;
import com.lvwj.halo.statemachine.builder.StateMachineBuilder;
import com.lvwj.halo.statemachine.builder.StateMachineBuilderFactory;
import com.lvwj.halo.statemachine.exception.TransitionFailException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 状态机注册
 *
 * @author lvweijie
 * @date 2023年11月17日 17:56
 */
public class StateMachineRegistry {

    @EventListener(value = ApplicationReadyEvent.class)
    public void registerStateMachine() {
        doRegisterStateMachine();
    }

    private void doRegisterStateMachine() {
        Collection<? extends StateProcessor> stateProcessors = SpringUtil.getBeansOfType(StateProcessor.class).values();
        Map<String, ? extends List<? extends StateProcessor>> map = stateProcessors.stream().collect(Collectors.groupingBy(StateProcessor::machineId));
        for (Map.Entry<String, ? extends List<? extends StateProcessor>> entry : map.entrySet()) {
            StateMachineBuilder builder = StateMachineBuilderFactory.create();
            builder.setFailCallback(new AlertFailCallback());
            for (StateProcessor processor : entry.getValue()) {
                List<StateTransition> transitions = processor.transitions();
                for (StateTransition transition : transitions) {
                    builder.externalTransitions()
                            .scenario(transition.scenario()) //业务场景
                            .from(transition.from()) //来源状态
                            .to(transition.to()) //目标状态
                            .on(transition.event()) //触发事件
                            .when(ctx -> {
                                if (!(ctx instanceof IContext)) {
                                    throw new TransitionFailException("C should implements IContext");
                                }
                                IContext sc = (IContext) ctx;
                                if (!isSatisfiedByScenario(transition.scenario(), sc.scenario(), sc.failover())) {
                                    return false;
                                }
                                return processor.isSatisfied(sc);
                            })
                            .perform(((from, to, event, ctx) -> {
                                IContext sc = (IContext) ctx;
                                try {
                                    processor.processBefore(from, to, event, sc);
                                    processor.process(from, to, event, sc);
                                    processor.processAfter(from, to, event, sc);
                                } catch (Exception e) {
                                    processor.processException(from, to, event, sc, e);
                                } finally {
                                    processor.processFinally(from, to, event, sc);
                                }
                            }));
                }
            }
            StateMachine stateMachine = builder.build(entry.getKey());
            stateMachine.showStateMachine();
        }
    }

    /**
     * @param scenario1 StateTransition's scenario
     * @param scenario2 IContext's scenario
     * @return boolean
     * @author lvweijie
     * @date 2023/11/18 11:00
     */
    private boolean isSatisfiedByScenario(BusinessScenario scenario1, BusinessScenario scenario2, boolean failover) {
        boolean result = false;
        if (scenario1.equals(scenario2)) {
            result = true;
        } else {
            //failover=true,允许降级判断场景是否满足
            if (failover) {
                if (scenario1.equals(BusinessScenario.of(scenario2.getBusiness(), scenario2.getUseCase()))) {
                    result = true;
                } else if (scenario1.equals(BusinessScenario.of(scenario2.getBusiness()))) {
                    result = true;
                } else if (scenario1.equals(BusinessScenario.of())) {
                    result = true;
                }
            }
        }
        return result;
    }
}
