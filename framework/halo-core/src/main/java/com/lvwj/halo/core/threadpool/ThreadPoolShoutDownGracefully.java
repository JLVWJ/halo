package com.lvwj.halo.core.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 优雅关闭线程池
 *
 * @author lvweijie
 * @date 2024年03月20日 14:51
 */
@Slf4j
@Component
public class ThreadPoolShoutDownGracefully {

    @EventListener(value = ContextClosedEvent.class)
    public void handle(ContextClosedEvent event) {
        ThreadPoolCache.shoutDownGracefully();
    }
}
