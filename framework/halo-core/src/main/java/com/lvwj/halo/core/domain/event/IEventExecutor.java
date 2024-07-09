package com.lvwj.halo.core.domain.event;

import com.lvwj.halo.common.utils.Exceptions;
import com.lvwj.halo.common.utils.TransactionUtil;
import lombok.Value;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件执行者
 */
public interface IEventExecutor {

  default <E extends IEvent> void submit(IEventHandler<E> handler, E event) {
    submit(new Task<>(handler, event));
  }

  <E extends IEvent> void submit(Task<E> task);

  class SyncExecutor implements IEventExecutor {

    @Override
    public <E extends IEvent> void submit(Task<E> task) {
      task.run();
    }
  }

  class AsyncExecutor implements IEventExecutor {

    private static final int CORE_THREAD = Runtime.getRuntime().availableProcessors() + 1;

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(CORE_THREAD, CORE_THREAD, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public <E extends IEvent> void submit(Task<E> task) {
      TransactionUtil.afterCommit(() -> POOL.submit(task));
    }
  }


  @Value
  class Task<E extends IEvent> implements Runnable {

    IEventHandler<E> handler;
    E event;

    @Override
    public void run() {
      try {
        this.handler.handle(this.event);
      } catch (Throwable e) {
        throw Exceptions.unchecked(e);
      }
    }
  }
}
