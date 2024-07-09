package com.lvwj.halo.distributedlock;

/**
 * 锁类型
 *
 * @author lcm
 */
public enum DistributedLockType {
  /**
   * 重入锁
   */
  REENTRANT,
  /**
   * 不重入锁
   */
  UN_REENTRANT,
  /**
   * 公平锁
   */
  FAIR,
  /**
   * 联锁
   */
  MULTI,
}
