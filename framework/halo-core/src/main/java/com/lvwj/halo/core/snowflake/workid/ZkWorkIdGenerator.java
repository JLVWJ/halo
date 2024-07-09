package com.lvwj.halo.core.snowflake.workid;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于zookeeper的机器ID生成器
 *
 * @author lvweijie
 * @date 2022/11/15 7:35 PM
 */
@Slf4j
public class ZkWorkIdGenerator implements Watcher, WorkIdGenerator {

  private static final int SESSION_TIMEOUT = 15000;
  private static final int INVALID_NODE_ID = -1;
  private static final String ROOT_PATH = "/snowflake-workId";
  private final String appPath;
  private final Integer workIdBits;

  private static int nodeId = INVALID_NODE_ID;
  private ZooKeeper zooKeeper;


  /**
   * @param zkNode     zk节点名称，一般为服务名
   * @param url        zk 地址
   * @param workIdBits 工作节点最多值
   */
  public ZkWorkIdGenerator(String zkNode, String url, Integer workIdBits) {
    this.appPath = ROOT_PATH + "/" + zkNode;
    this.workIdBits = workIdBits;
    try {
      this.zooKeeper = new ZooKeeper(url, SESSION_TIMEOUT, this);
    } catch (IOException e) {
      log.error("连接Zookeeper失败", e);
    }
  }

  @Override
  public void process(WatchedEvent event) {
    log.info("ZkWorkIdGenerator机器节点：{}", event);
    if (event.getType() == EventType.NodeDeleted) {
      nodeId = INVALID_NODE_ID;
      log.info("ZkWorkIdGenerator机器节点被删除：{}", event.getPath());
    }
  }

  /**
   * 基于 workIdBits 创建节点
   *
   * @throws InterruptedException InterruptedException
   * @throws KeeperException      KeeperException
   */
  private void createNodes() throws KeeperException, InterruptedException {
    createNodePersistent(ROOT_PATH);
    createNodePersistent(appPath);

    int maxWorkId = (1 << workIdBits) - 1;
    for (int i = 0; i <= maxWorkId; i++) {
      String strInt = String.format("%02d", i);
      createNodePersistent(appPath + "/" + strInt);
    }
  }

  /**
   * 创建永久节点
   *
   * @param path zk path
   * @throws KeeperException      KeeperException
   * @throws InterruptedException InterruptedException
   */
  private void createNodePersistent(String path) throws KeeperException, InterruptedException {
    Stat stat = zooKeeper.exists(path, false);
    if (stat == null) {
      zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
  }

  /**
   * 获取机器id
   *
   * @return 机器ID
   */
  public synchronized long getWorkId() {
    if (nodeId != INVALID_NODE_ID) {
      return nodeId;
    }
    try {
      Stat stat = zooKeeper.exists(appPath, false);
      if (stat == null) {
        createNodes();
      }
      List<String> idNodes = zooKeeper.getChildren(appPath, false);
      if (idNodes == null || idNodes.isEmpty()) {
        throw new RuntimeException("生成机器id失败, idNodes is empty");
      }
      List<Integer> nodes = idNodes.stream().map(Integer::valueOf).sorted().collect(Collectors.toList());
      for (Integer node : nodes) {
        String idPath = appPath + "/" + node + "/node";
        Stat statN = zooKeeper.exists(idPath, false);
        if (statN == null) {
          try {
            zooKeeper.create(idPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            zooKeeper.exists(idPath, this);
            nodeId = node;
            break;
          } catch (Exception e) {
            //log.error("从zookeeper获取workId异常", e);
          }
        }
      }
      if (nodeId < 0) {
        throw new RuntimeException("生成机器id失败");
      }
    } catch (Exception e) {
      log.error("从zookeeper获取workId异常", e);
    }
    return nodeId;
  }
}
