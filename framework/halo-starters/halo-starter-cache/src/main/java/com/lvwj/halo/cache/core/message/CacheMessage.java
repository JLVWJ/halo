package com.lvwj.halo.cache.core.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 缓存消息实体
 *
 * @author lvwj
 * @date 2022-08-18 14:25
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CacheMessage implements Serializable {

  private String name;
  private Object key;
  private Object value;
  private CacheMessageType type;  //标识更新或删除操作
  private String ip;   //源主机ip，用来避免重复操作
}
