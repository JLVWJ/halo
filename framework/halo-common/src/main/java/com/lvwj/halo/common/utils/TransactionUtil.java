package com.lvwj.halo.common.utils;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务工具类
 */
public class TransactionUtil {

    /**
     * 当前环境存在事物则延迟至事物提交后执行，否则立即执行
     *
     */
    public static void afterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
            return;
        }
        runnable.run();
    }

    /**
     * 有事务则事务完成之后执行方法，无事务则直接执行方法
     *
     */
    public static void afterCompletion(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            runnable.run();
                        }
                    });
        } else {
            runnable.run();
        }
    }

    /**
     * 有事务则事务回滚之后执行方法，无事务则不执行方法
     *
     */
    public static void afterRollBack(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == TransactionSynchronization.STATUS_ROLLED_BACK || status == TransactionSynchronization.STATUS_UNKNOWN) {
                                runnable.run();
                            }
                        }
                    });
        }
    }
}
