package com.lvwj.halo.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lvwj.halo.common.enums.BaseErrorEnum;
import com.lvwj.halo.common.function.checked.CheckedRunnable;
import com.lvwj.halo.common.function.checked.CheckedSupplier;
import com.lvwj.halo.common.utils.Assert;
import com.lvwj.halo.common.utils.Exceptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * RedisTemplate增强
 *
 * @author lvwj
 * @date 2022-08-16 10:53
 */
@Slf4j
public class RedisTemplatePlus {

  private static final int BATCH_SIZE = 500;

  private static final int DEFAULT_EXPIRE_SECONDS = 30;

  @Getter
  private final RedisTemplate<String, Object> redisTemplate;

  private final ValueOperations<String, Object> valueOps;
  private final HashOperations<String, Object, Object> hashOps;
  private final ListOperations<String, Object> listOps;
  private final SetOperations<String, Object> setOps;
  private final ZSetOperations<String, Object> zSetOps;

  private final DefaultRedisScript<Long> rateLimiterScript;

  private static final String RATE_LIMIT_KEY_PREFIX = "RATE_LIMIT:";
  private static final String IDEMPOTENT_KEY_PREFIX = "IDEMPOTENT:";

  public RedisTemplatePlus(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.valueOps = redisTemplate.opsForValue();
    this.hashOps = redisTemplate.opsForHash();
    this.listOps = redisTemplate.opsForList();
    this.setOps = redisTemplate.opsForSet();
    this.zSetOps = redisTemplate.opsForZSet();

    this.rateLimiterScript = new DefaultRedisScript<>();
    this.rateLimiterScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/rate_limiter.lua")));
    this.rateLimiterScript.setResultType(Long.class);
  }

  private final Function<Long, Long> TIME_RANDOM = sec -> sec + ThreadLocalRandom.current().nextLong(1, 10);

  /**
   * 存放 key value 对到 redis。 默认过期时间30秒
   */
  public void set(String key, Object value) {
    valueOps.set(RedisKeyGenerator.gen(key), value);
  }

  /**
   * 存放 key value 对到 redis
   */
  public void set(String key, Object value, Duration timeout) {
    valueOps.set(RedisKeyGenerator.gen(key), value, timeout);
  }

  /**
   * 存放 key value 对到 redis，并将 key 的生存时间设为 seconds (以秒为单位)。
   */
  public void set(String key, Object value, long seconds) {
    this.set(key, value, seconds, TimeUnit.SECONDS);
  }

  public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
    if (timeout == 0) {
      this.set(key, value);
      return;
    }
    if (timeout < 0) {
      timeout = DEFAULT_EXPIRE_SECONDS;
      timeUnit = TimeUnit.SECONDS;
    }
    if (timeUnit != TimeUnit.SECONDS) {
      timeout = timeUnit.toSeconds(timeout);
      timeUnit = TimeUnit.SECONDS;
    }
    valueOps.set(RedisKeyGenerator.gen(key), value, TIME_RANDOM.apply(timeout), timeUnit);
  }

  /**
   * 存放 key value 对到 redis 如果 key 已经存在， 将返回false
   */
  public boolean setNx(String key, Object value) {
    return Boolean.TRUE.equals(valueOps.setIfAbsent(RedisKeyGenerator.gen(key), value));
  }

  /**
   * 存放 key value 对到 redis，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， 将返回false
   */
  public boolean setNx(String key, Object value, long seconds) {
    return this.setNx(key, value, seconds, TimeUnit.SECONDS);
  }

  public boolean setNx(String key, Object value, long timeout, TimeUnit timeUnit) {
    if (timeout == 0) {
      return this.setNx(key, value);
    }
    if (timeout < 0) {
      timeout = DEFAULT_EXPIRE_SECONDS;
      timeUnit = TimeUnit.SECONDS;
    }
    if (timeUnit != TimeUnit.SECONDS) {
      timeout = timeUnit.toSeconds(timeout);
      timeUnit = TimeUnit.SECONDS;
    }
    return Boolean.TRUE.equals(valueOps.setIfAbsent(RedisKeyGenerator.gen(key), value, TIME_RANDOM.apply(timeout), timeUnit));
  }

  /**
   * 如果存在key，则设置key以保存字符串值。
   */
  public boolean setXx(String key, Object value) {
    return Boolean.TRUE.equals(valueOps.setIfPresent(RedisKeyGenerator.gen(key), value));
  }

  /**
   * 如果存在key，则设置key以保存字符串值。
   */
  public boolean setXx(String key, Object value, long seconds) {
    return this.setXx(key, value, seconds, TimeUnit.SECONDS);
  }

  /**
   * 如果存在key，则设置key以保存字符串值。
   */
  public boolean setXx(String key, Object value, long timeout, TimeUnit timeUnit) {
    if (timeout == 0) {
      return this.setXx(key, value);
    }
    if (timeout < 0) {
      timeout = DEFAULT_EXPIRE_SECONDS;
      timeUnit = TimeUnit.SECONDS;
    }
    if (timeUnit != TimeUnit.SECONDS) {
      timeout = timeUnit.toSeconds(timeout);
      timeUnit = TimeUnit.SECONDS;
    }
    return Boolean.TRUE.equals(valueOps.setIfPresent(RedisKeyGenerator.gen(key), value, TIME_RANDOM.apply(timeout), timeUnit));
  }

  /**
   * 返回 key 所关联的 value 值 如果 key 不存在那么返回特殊值 nil 。
   */
  public <T> T get(String key) {
    return (T) valueOps.get(RedisKeyGenerator.gen(key));
  }

  /**
   * 获取cache 为 null 时使用加载器，然后设置缓存
   *
   * @param key    cacheKey
   * @param loader cache loader
   * @param <T>    泛型
   * @return 结果
   */
  public <T> T get(String key, Supplier<T> loader) {
    return get(key, loader, 0);
  }

  /**
   * 获取cache 为 null 时使用加载器，然后设置缓存
   *
   * @param key     cacheKey
   * @param loader  cache loader
   * @param seconds 过期时间
   * @param <T>     泛型
   * @return 结果
   */
  public <T> T get(String key, Supplier<T> loader, long seconds) {
    return get(key, loader, false, seconds);
  }

  /**
   * 获取cache 为 null 时使用加载器，然后设置缓存
   *
   * @author lvweijie
   * @date 2023/11/16 21:55
   * @param key key
   * @param loader 数据加载器(从数据库加载数据)
   * @param cacheNull 是否缓存空值
   * @param seconds 过期时间
   * @return T
   */
  public <T> T get(String key, Supplier<T> loader, boolean cacheNull, long seconds) {
    T value = this.get(key);
    if (value != null) {
      if (value instanceof String && value.toString().equals("nil")) {
        return null;
      }
      return value;
    }
    value = loader.get();
    if (value == null) {
      if (cacheNull) {
        this.set(key, "nil", 3);//缓存空值
      }
      return null;
    }
    this.set(key, value, seconds);
    return value;
  }

  /**
   * 删除给定的一个 key 不存在的 key 会被忽略。
   */
  public Boolean del(String key) {
    return redisTemplate.delete(RedisKeyGenerator.gen(key));
  }

  /**
   * 删除给定的多个 key 不存在的 key 会被忽略。
   */
  public Long del(String... keys) {
    return del(Arrays.asList(keys));
  }

  /**
   * 删除给定的多个 key 不存在的 key 会被忽略。
   */
  public Long del(Collection<String> keys) {
    if (CollUtil.isEmpty(keys)) {
      return 0L;
    }
    List<String> keyList = keys.stream().map(RedisKeyGenerator::gen).collect(Collectors.toList());
    List<List<String>> partitionKeys = Lists.partition(keyList, BATCH_SIZE);
    long count = 0;
    for (List<String> list : partitionKeys) {
      Long result = redisTemplate.delete(list);
      if (null != result) {
        count += result;
      }
    }
    return count;
  }

  /**
   * 异步删除
   */
  public Long unlink(@NonNull String... keys) {
    return unlink(Arrays.stream(keys).collect(Collectors.toList()));
  }

  /**
   * 异步删除
   */
  public Long unlink(@NonNull Collection<String> keys) {
    if (CollUtil.isEmpty(keys)) {
      return 0L;
    }
    List<String> keyList = keys.stream().map(RedisKeyGenerator::gen).collect(Collectors.toList());
    List<List<String>> partitionKeys = Lists.partition(keyList, BATCH_SIZE);
    long count = 0;
    for (List<String> list : partitionKeys) {
      Long result = redisTemplate.unlink(list);
      if (null != result) {
        count += result;
      }
    }
    return count;
  }

  /**
   * 批量扫描匹配到的key，然后异步删除
   *
   * @author lvwj
   * @date 2023-01-10 00:10
   */
  public void scanUnlink(@NonNull String pattern) {
    if (StrUtil.isEmpty(pattern) || "*".equals(pattern.trim())) {
      throw new RuntimeException("必须指定匹配符");
    }
    List<String> keys = scan(pattern);
    if (CollUtil.isEmpty(keys)) {
      return;
    }
    unlink(keys);
  }

  /**
   * 查找所有符合给定模式 pattern 的 key 。
   * <p>
   * 例子： KEYS * 匹配数据库中所有 key 。 KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。 KEYS h*llo 匹配 hllo 和 heeeeello 等。 KEYS h[ae]llo 匹配
   * hello 和 hallo ，但不匹配 hillo 。
   * <p>
   * 特殊符号用 \ 隔开
   *
   * @param pattern 表达式
   * @return 符合给定模式的 key 列表
   * @see <a href="https://redis.io/commands/keys">Redis Documentation: KEYS</a>
   */
  public List<String> scan(@NonNull String pattern) {
    List<String> keyList = new ArrayList<>();
    scan(pattern, item -> {
      //符合条件的key
      Object key = redisTemplate.getKeySerializer().deserialize(item);
      if (ObjectUtil.isNotEmpty(key)) {
        keyList.add(String.valueOf(key));
      }
    });
    return keyList;
  }

  /**
   * scan 实现
   *
   * @param pattern  表达式
   * @param consumer 对迭代到的key进行操作
   */
  private void scan(String pattern, Consumer<byte[]> consumer) {
    redisTemplate.execute((RedisConnection connection) -> {
      try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(BATCH_SIZE).match(pattern).build())) {
        cursor.forEachRemaining(consumer);
        return null;
      }
    });
  }

  /**
   * 同时为一个或多个键设置值。 如果某个给定键已经存在， 那么 MSET 将使用新值去覆盖旧值， 如果这不是你所希望的效果， 请考虑使用 MSETNX 命令， 这个命令只会在所有给定键都不存在的情况下进行设置。 MSET
   * 是一个原子性(atomic)操作， 所有给定键都会在同一时间内被设置， 不会出现某些键被设置了但是另一些键没有被设置的情况。
   */
  public void mSet(Map<String, Object> map) {
    Map<String, Object> genMap = new HashMap<>(map.size());
    map.forEach((k, v) -> genMap.put(RedisKeyGenerator.gen(k), v));
    valueOps.multiSet(genMap);
  }

  /**
   * 当且仅当所有给定键都不存在时， 为所有给定键设置值。 即使只有一个给定键已经存在， MSETNX 命令也会拒绝执行对所有键的设置操作。 MSETNX 是一个原子性(atomic)操作， 所有给定键要么就全部都被设置，
   * 要么就全部都不设置， 不可能出现第三种状态。
   */
  public void mSetNx(Map<String, Object> map) {
    Map<String, Object> genMap = new HashMap<>(map.size());
    map.forEach((k, v) -> genMap.put(RedisKeyGenerator.gen(k), v));
    valueOps.multiSetIfAbsent(genMap);
  }

  /**
   * 返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
   */
  public <T> List<T> mGet(String... keys) {
    return mGet(Arrays.asList(keys));
  }

  /**
   * 返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
   */
  public <T> List<T> mGet(Collection<String> keys) {
    List<String> keyList = keys.stream().map(RedisKeyGenerator::gen).collect(Collectors.toList());
    return (List<T>) valueOps.multiGet(keyList);
  }

  /**
   * 将 key 中储存的数字值减一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。 关于递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
   */
  public Long decr(String key) {
    return valueOps.decrement(RedisKeyGenerator.gen(key));
  }

  /**
   * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
   * 本操作的值限制在 64 位(bit)有符号数字表示之内。 关于更多递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
   */
  public Long decrBy(String key, long longValue) {
    return valueOps.decrement(RedisKeyGenerator.gen(key), longValue);
  }

  /**
   * 将 key 中储存的数字值增一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64
   * 位(bit)有符号数字表示之内。
   */
  public Long incr(String key) {
    return valueOps.increment(RedisKeyGenerator.gen(key));
  }

  /**
   * 将 key 所储存的值加上增量 increment 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
   * 本操作的值限制在 64 位(bit)有符号数字表示之内。 关于递增(increment) / 递减(decrement)操作的更多信息，参见 INCR 命令。
   */
  public Long incrBy(String key, long longValue) {
    return valueOps.increment(RedisKeyGenerator.gen(key), longValue);
  }

  /**
   * 检查给定 key 是否存在。
   */
  public Boolean exists(String key) {
    return redisTemplate.hasKey(RedisKeyGenerator.gen(key));
  }

  /**
   * 如果键 key 已经存在并且它的值是一个字符串， APPEND 命令将把 value 追加到键 key 现有值的末尾。 如果 key 不存在， APPEND 就简单地将键 key 的值设为 value ， 就像执行 SET key
   * value 一样。
   */
  public Integer append(@NonNull String key, String value) {
    return valueOps.append(RedisKeyGenerator.gen(key), value);
  }

  /**
   * 从当前数据库中随机返回(不删除)一个 key 。
   */
  public String randomKey() {
    return redisTemplate.randomKey();
  }

  /**
   * 将 oldKey 改名为 newKey 。 当 oldKey 和 newKey 相同，或者 oldKey 不存在时，返回一个错误。 当 newKey 已经存在时， RENAME 命令将覆盖旧值。
   */
  public void rename(@NonNull String oldKey, @NonNull String newKey) {
    redisTemplate.rename(RedisKeyGenerator.gen(oldKey), RedisKeyGenerator.gen(newKey));
  }

  /**
   * 当且仅当 newKey 不存在时，将 oldKey 改名为 newKey 。
   *
   * @param oldKey 一定不能为 {@literal null}.
   * @param newKey 一定不能为 {@literal null}.
   * @return 是否成功
   * @see <a href="https://redis.io/commands/renamenx">Redis Documentation: RENAMENX</a>
   */
  public Boolean renameNx(@NonNull String oldKey, @NonNull String newKey) {
    return redisTemplate.renameIfAbsent(oldKey, newKey);
  }

  /**
   * 将当前数据库的 key 移动到给定的数据库 db 当中。 如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
   * 因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)。
   *
   * @param key     一定不能为 {@literal null}.
   * @param dbIndex 数据库索引
   * @return 是否成功
   * @see <a href="https://redis.io/commands/move">Redis Documentation: MOVE</a>
   */
  public Boolean move(@NonNull String key, int dbIndex) {
    return redisTemplate.move(key, dbIndex);
  }

  /**
   * 为给定 key 设置生存时间(秒)，当 key 过期时(生存时间为0)，它会被自动删除。
   */
  public Boolean expire(String key, long seconds) {
    return this.expire(key, seconds, TimeUnit.SECONDS);
  }

  /**
   * 为给定 key 设置生存时间(秒)，当 key 过期时(生存时间为0)，它会被自动删除。
   */
  public Boolean expire(String key, Duration timeout) {
    return expire(key, timeout.getSeconds());
  }

  public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
    return redisTemplate.expire(RedisKeyGenerator.gen(key), timeout, timeUnit);
  }

  /**
   * 为给定 key 设置生存时间(毫秒)，当 key 过期时(生存时间为0)，它会被自动删除。
   */
  public Boolean pExpire(@NonNull String key, long milliseconds) {
    return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
  }

  /**
   * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
   */
  public Boolean expireAt(String key, Date date) {
    return redisTemplate.expireAt(RedisKeyGenerator.gen(key), date);
  }

  /**
   * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
   */
  public Boolean expireAt(String key, long unixTime) {
    return expireAt(key, new Date(unixTime));
  }

  /**
   * 返回给定key的超时秒数
   */
  public Long ttl(String key) {
    return redisTemplate.getExpire(RedisKeyGenerator.gen(key), TimeUnit.SECONDS);
  }

  /**
   * 返回给定key的超时毫秒数
   */
  public Long pTtl(@NonNull String key) {
    return redisTemplate.getExpire(RedisKeyGenerator.gen(key), TimeUnit.MILLISECONDS);
  }

  /**
   * 移除给定key的生存时间，将这个key从『易失的』(带生存时间key)转换成『持久的』(一个不带生存时间、永不过期的 key)。
   */
  public Boolean persist(@NonNull String key) {
    return redisTemplate.persist(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回 key 所储存的值的类型。
   *
   * @param key 一定不能为 {@literal null}.
   * @return none (key不存在)、string (字符串)、list (列表)、set (集合)、zset (有序集)、hash (哈希表) 、stream （流）
   * @see <a href="https://redis.io/commands/type">Redis Documentation: TYPE</a>
   */
  public String type(@NonNull String key) {
    DataType type = redisTemplate.type(key);
    return type == null ? DataType.NONE.code() : type.code();
  }

  /**
   * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。 如果域 field 已经存在于哈希表中，旧值将被覆盖。
   */
  public void hSet(String key, Object field, Object value) {
    hashOps.put(RedisKeyGenerator.gen(key), field, value);
  }

  /**
   * 同时将多个 field-value (域-值)对设置到哈希表 key 中。 此命令会覆盖哈希表中已存在的域。 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
   */
  public void hMSet(String key, Map<Object, Object> hash) {
    hashOps.putAll(RedisKeyGenerator.gen(key), hash);
  }

  /**
   * 返回哈希表 key 中给定域 field 的值。
   */
  public <T> T hGet(String key, Object field) {
    return (T) hashOps.get(RedisKeyGenerator.gen(key), field);
  }

  /**
   * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil
   * 值的表。
   */
  public List<Object> hmGet(String key, Object... fields) {
    return hmGet(key, Arrays.asList(fields));
  }

  /**
   * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil
   * 值的表。
   */
  public List<Object> hmGet(String key, Collection<Object> hashKeys) {
    return hashOps.multiGet(RedisKeyGenerator.gen(key), hashKeys);
  }

  /**
   * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
   */
  public Long hDel(String key, Object... fields) {
    return hashOps.delete(RedisKeyGenerator.gen(key), fields);
  }

  /**
   * 查看哈希表 key 中，给定域 field 是否存在。
   */
  public Boolean hExists(String key, Object field) {
    return hashOps.hasKey(RedisKeyGenerator.gen(key), field);
  }

  /**
   * 返回哈希表 key 中，所有的域和值。 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
   */
  public Map<Object, Object> hGetAll(String key) {
    return hashOps.entries(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回哈希表 key 中所有域的值。
   */
  public <HV> List<HV> hValues(@NonNull String key) {
    return (List<HV>) hashOps.values(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回哈希表 key 中的所有域。
   */
  public <HK> Set<HK> hKeys(@NonNull String key) {
    return (Set<HK>) hashOps.keys(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回哈希表 key 中域的数量。
   */
  public Long hLen(String key) {
    return hashOps.size(RedisKeyGenerator.gen(key));
  }

  /**
   * 为哈希表 key 中的域 field 的值加上增量 increment 。 增量也可以为负数，相当于对给定域进行减法操作。 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。 如果域 field
   * 不存在，那么在执行命令前，域的值被初始化为 0 。 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。 本操作的值被限制在 64 位(bit)有符号数字表示之内。
   */
  public Long hIncrBy(String key, Object field, long value) {
    return hashOps.increment(RedisKeyGenerator.gen(key), field, value);
  }

  /**
   * 为哈希表 key 中的域 field 加上浮点数增量 increment 。 如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。 如果键 key
   * 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。 当以下任意一个条件发生时，返回一个错误： 1:域 field 的值不是字符串类型(因为 redis
   * 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型） 2:域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point
   * number) HINCRBYFLOAT 命令的详细功能和 INCRBYFLOAT 命令类似，请查看 INCRBYFLOAT 命令获取更多相关信息。
   */
  public Double hIncrByFloat(String key, Object field, double value) {
    return hashOps.increment(RedisKeyGenerator.gen(key), field, value);
  }

  /**
   * 返回列表 key 中，下标为 index 的元素。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素， 以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以
   * -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 如果 key 不是列表类型，返回一个错误。
   */
  public <T> T lIndex(String key, long index) {
    return (T) listOps.index(RedisKeyGenerator.gen(key), index);
  }

  /**
   * 返回列表 key 的长度。 如果 key 不存在，则 key 被解释为一个空列表，返回 0 . 如果 key 不是列表类型，返回一个错误。
   */
  public Long lLen(String key) {
    return listOps.size(RedisKeyGenerator.gen(key));
  }

  /**
   * 移除并返回列表 key 的头元素。
   */
  public <T> T lPop(String key) {
    return (T) listOps.leftPop(RedisKeyGenerator.gen(key));
  }

  public <T> List<T> lPop(String key, int count) {
    if (count < 1) {
      count = 1;
    }
    List<T> list = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      T o = lPop(key);
      if (null != o) {
        list.add(o);
      } else {
        break;
      }
    }
    return list;
  }

  public <T> List<T> lPop(String key, long count) {
    return (List<T>) listOps.leftPop(RedisKeyGenerator.gen(key), count);
  }

  /**
   * 移除并返回列表 key 的头元素, 如果没有 则阻塞等待。
   */
  public <T> T blPop(String key, long timeout, TimeUnit timeUnit) {
    return (T) listOps.leftPop(RedisKeyGenerator.gen(key), timeout, timeUnit);
  }

  /**
   * 将一个或多个值 value 插入到列表 key 的表头
   */
  public Long lPush(String key, Object value) {
    return listOps.leftPush(RedisKeyGenerator.gen(key), value);
  }

  /**
   * 将多个值 value 插入到列表 key 的表头 LPUSHALL list - a b c ，列表的值将是 c b a ，
   */
  public Long lPushAll(String key, Object... values) {
    return listOps.leftPushAll(RedisKeyGenerator.gen(key), values);
  }

  /**
   * 将多个值 value 插入到列表 key 的表头 LPUSHALL list - a b c ，列表的值将是 c b a ，
   */
  public Long lPushAll(String key, Collection<?> values) {
    return listOps.leftPushAll(RedisKeyGenerator.gen(key), new ArrayList<>(values));
  }

  /**
   * 将列表 key 下标为 index 的元素的值设置为 value 。 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。 关于列表下标的更多信息，请参考 LINDEX 命令。
   */
  public void lSet(String key, long index, Object value) {
    listOps.set(RedisKeyGenerator.gen(key), index, value);
  }

  /**
   * 根据参数 count 的值，移除列表中与参数 value 相等的元素。 count 的值可以是以下几种： count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。 count < 0 :
   * 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。 count = 0 : 移除表中所有与 value 相等的值。
   */
  public Long lRem(String key, long count, Object value) {
    return listOps.remove(RedisKeyGenerator.gen(key), count, value);
  }

  /**
   * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1
   * 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
   * <pre>
   * 例子：
   * 获取 list 中所有数据：cache.lrange(listKey, 0, -1);
   * 获取 list 中下标 1 到 3 的数据： cache.lrange(listKey, 1, 3);
   * </pre>
   */
  public List<Object> lRange(String key, long start, long end) {
    return listOps.range(RedisKeyGenerator.gen(key), start, end);
  }

  /**
   * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
   * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2
   * 表示列表的倒数第二个元素，以此类推。 当 key 不是列表类型时，返回一个错误。
   */
  public void lTrim(String key, long start, long end) {
    listOps.trim(RedisKeyGenerator.gen(key), start, end);
  }

  /**
   * 移除并返回列表 key 的尾元素。
   */
  public <T> T rPop(String key) {
    return (T) listOps.rightPop(RedisKeyGenerator.gen(key));
  }

  /**
   * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
   */
  public Long rPush(String key, Object value) {
    return listOps.rightPush(RedisKeyGenerator.gen(key), value);
  }

  /**
   * 将多个值 value 插入到列表 key 的表头 RPUSHALL list
   */
  public Long rPushAll(String key, Object... values) {
    return listOps.rightPushAll(RedisKeyGenerator.gen(key), values);
  }

  /**
   * 将多个值 value 插入到列表 key 的表头 RPUSHALL list
   */
  public Long rPushAll(String key, Collection<?> values) {
    return listOps.rightPushAll(RedisKeyGenerator.gen(key), new ArrayList<>(values));
  }

  /**
   * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作： 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。 将 source 弹出的元素插入到列表 destination ，作为 destination
   * 列表的的头元素。
   */
  public <T> T rPopLPush(String srcKey, String dstKey) {
    return (T) listOps.rightPopAndLeftPush(RedisKeyGenerator.gen(srcKey),
        RedisKeyGenerator.gen(dstKey));
  }

  /**
   * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。 当 key 不是集合类型时，返回一个错误。
   */
  public Long sAdd(String key, Object... members) {
    return setOps.add(RedisKeyGenerator.gen(key), members);
  }

  /**
   * 移除并返回集合中的一个随机元素。 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
   */
  public <T> T sPop(String key) {
    return (T) setOps.pop(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
   */
  public Set<Object> sMembers(String key) {
    return setOps.members(RedisKeyGenerator.gen(key));
  }

  /**
   * 判断 member 元素是否集合 key 的成员。
   */
  public Boolean sIsMember(String key, Object member) {
    return setOps.isMember(RedisKeyGenerator.gen(key), member);
  }

  /**
   * 返回多个集合的交集，多个集合由 keys 指定
   */
  public <T> Set<T> sInter(String key, String otherKey) {
    return (Set<T>)setOps.intersect(RedisKeyGenerator.gen(key), otherKey);
  }

  /**
   * 返回多个集合的交集，多个集合由 keys 指定
   */
  public <T> Set<T> sInter(String key, Collection<String> otherKeys) {
    return (Set<T>)setOps.intersect(RedisKeyGenerator.gen(key), otherKeys);
  }

  /**
   * 返回集合中的一个随机元素。
   */
  public <T> T sRandMember(String key) {
    return (T) setOps.randomMember(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回集合中的 count 个随机元素。 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数： 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count
   * 个元素的数组，数组中的元素各不相同。 如果 count 大于等于集合基数，那么返回整个集合。 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。 该操作和
   * SPOP 相似，但 SPOP 将随机元素从集合中移除并返回，而 SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
   */
  public <T> List<T> sRandMember(String key, int count) {
    return (List<T>)setOps.randomMembers(RedisKeyGenerator.gen(key), count);
  }

  /**
   * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
   */
  public Long sRem(String key, Object... members) {
    return setOps.remove(RedisKeyGenerator.gen(key), members);
  }

  /**
   * 返回多个集合的并集，多个集合由 keys 指定 不存在的 key 被视为空集。
   */
  public <T> Set<T> sUnion(String key, String otherKey) {
    return (Set<T>)setOps.union(RedisKeyGenerator.gen(key), otherKey);
  }

  /**
   * 返回多个集合的并集，多个集合由 keys 指定 不存在的 key 被视为空集。
   */
  public <T> Set<T> sUnion(String key, Collection<String> otherKeys) {
    return (Set<T>)setOps.union(RedisKeyGenerator.gen(key), otherKeys);
  }

  /**
   * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。 不存在的 key 被视为空集。
   */
  public <T> Set<T> sDiff(String key, String otherKey) {
    return (Set<T>)setOps.difference(RedisKeyGenerator.gen(key), otherKey);
  }

  /**
   * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。 不存在的 key 被视为空集。
   */
  public <T> Set<T> sDiff(String key, Collection<String> otherKeys) {
    return (Set<T>)setOps.difference(RedisKeyGenerator.gen(key), otherKeys);
  }

  /**
   * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值， 并通过重新插入这个 member 元素，来保证该
   * member 在正确的位置上。
   */
  public Boolean zAdd(String key, Object member, double score) {
    return zSetOps.add(RedisKeyGenerator.gen(key), member, score);
  }

  /**
   * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值， 并通过重新插入这个 member 元素，来保证该
   * member 在正确的位置上。
   */
  public Long zAdd(String key, Map<Object, Double> scoreMembers) {
    Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
    scoreMembers.forEach((k, v) -> {
      tuples.add(new DefaultTypedTuple<>(k, v));
    });
    return zSetOps.add(RedisKeyGenerator.gen(key), tuples);
  }

  /**
   * 返回有序集 key 的基数。
   */
  public Long zCard(String key) {
    return zSetOps.zCard(RedisKeyGenerator.gen(key));
  }

  /**
   * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。 关于参数 min 和 max 的详细使用方法，请参考 ZRANGEBYSCORE 命令。
   */
  public Long zCount(String key, double min, double max) {
    return zSetOps.count(RedisKeyGenerator.gen(key), min, max);
  }

  /**
   * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
   */
  public Double zIncrBy(String key, Object member, double score) {
    return zSetOps.incrementScore(RedisKeyGenerator.gen(key), member, score);
  }

  /**
   * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递增(从小到大)来排序。 具有相同 score 值的成员按字典序(lexicographical order )来排列。 如果你需要成员按 score
   * 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
   */
  public <T> Set<T> zRange(String key, long start, long end) {
    return (Set<T>)zSetOps.range(RedisKeyGenerator.gen(key), start, end);
  }

  /**
   * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。 除了成员按
   * score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
   */
  public <T> Set<T> zRevRange(String key, long start, long end) {
    return (Set<T>)zSetOps.reverseRange(RedisKeyGenerator.gen(key), start, end);
  }

  /**
   * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。 有序集成员按 score 值递增(从小到大)次序排列。
   */
  public <T> Set<T> zRangeByScore(String key, double min, double max) {
    return (Set<T>)zSetOps.rangeByScore(RedisKeyGenerator.gen(key), min, max);
  }

  /**
   * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。 使用 ZREVRANK 命令可以获得成员按
   * score 值递减(从大到小)排列的排名。
   */
  public Long zRank(String key, Object member) {
    return zSetOps.rank(RedisKeyGenerator.gen(key), member);
  }

  /**
   * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。 使用 ZRANK 命令可以获得成员按 score
   * 值递增(从小到大)排列的排名。
   */
  public Long zRevRank(String key, Object member) {
    return zSetOps.reverseRank(RedisKeyGenerator.gen(key), member);
  }

  /**
   * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。 当 key 存在但不是有序集类型时，返回一个错误。
   */
  public Long zRem(String key, Object... members) {
    return zSetOps.remove(RedisKeyGenerator.gen(key), members);
  }

  /**
   * 移除有序集 key 中的多个成员，分数区间内的key将被移除
   */
  public Long zRemRangeByScore(String key, double min, double max) {
    return zSetOps.removeRangeByScore(RedisKeyGenerator.gen(key), min, max);
  }

  /**
   * 返回有序集 key 中，成员 member 的 score 值。 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
   */
  public Double zScore(String key, Object member) {
    return zSetOps.score(RedisKeyGenerator.gen(key), member);
  }

  // ------ BitMap ------

  /**
   * 位图功能
   *
   * @param key    键
   * @param offset 偏移量
   * @param value  值
   * @return boolean
   */
  public boolean setBit(String key, long offset, boolean value) {
    return Boolean.TRUE.equals(valueOps.setBit(RedisKeyGenerator.gen(key), offset, value));
  }

  /**
   * 获取位图某偏移量
   *
   * @param key    键
   * @param offset 偏移量
   * @return boolean
   */
  public boolean getBit(String key, long offset) {
    return Boolean.TRUE.equals(valueOps.getBit(RedisKeyGenerator.gen(key), offset));
  }

  // ------ pubsub ------

  public void convertAndSend(String topic, Object message) {
    redisTemplate.convertAndSend(topic, message);
  }

  // ------ script ------

  /**
   * @param script 执行脚本
   * @param keys   键
   * @param args   参数数组
   * @param <T>    泛型
   * @return T
   */
  public <T> T execute(RedisScript<T> script, List<String> keys, Object... args) {
    List<String> keyList = keys.stream().map(RedisKeyGenerator::gen).collect(Collectors.toList());
    return redisTemplate.execute(script, keyList, args);
  }

  // ------ pipeline ------

  /**
   * 流水线-MSetEx
   *
   * @param keyValues keyValues
   * @param seconds   过期时间
   * @author lvwj
   * @date 2022-08-17 09:30
   */
  public void pipeMSetEx(Map<String, Object> keyValues, Long seconds) {
    if (CollectionUtils.isEmpty(keyValues)) {
      return;
    }
    try {
      this.redisTemplate.executePipelined(new SessionCallback<String>() {
        @Override
        public String execute(RedisOperations operations) throws DataAccessException {
          for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
            operations.opsForValue().set(RedisKeyGenerator.gen(entry.getKey()), entry.getValue(), TIME_RANDOM.apply(seconds), TimeUnit.SECONDS);
          }
          return null;
        }
      });
    } catch (Exception e) {
      log.error("pipeMSetEx fail! ", e);
    }
  }

  /**
   * 流水线-HGetAll
   *
   * @param codes  codes
   * @return java.util.Map<java.lang.String, java.util.Map < java.lang.Object, java.lang.Object>>
   * @author lvwj
   * @date 2022-08-17 09:44
   */
  public Map<String, Map<Object, Object>> pipeHGetAll(List<String> codes) {
    Map<String, Map<Object, Object>> result = new HashMap<>();
    if (CollectionUtils.isEmpty(codes)) {
      return result;
    }
    try {
      List<Object> list = this.redisTemplate.executePipelined(new SessionCallback<String>() {
        @Override
        public String execute(RedisOperations operations) throws DataAccessException {
          for (String code : codes) {
            operations.opsForHash().entries(RedisKeyGenerator.gen(code));
          }
          return null;
        }
      });
      int i = 0;
      for (Object o : list) {
        if (null != o) {
          result.put(codes.get(i++), (Map<Object, Object>) o);
        }
      }
    } catch (Exception e) {
      log.error("pipeHGetAll fail! e:{}", e.getMessage(), e);
    }
    return result;
  }


  // ------ idempotent ------

  /**
   * 幂等 （方法级，编程式的幂等方法）
   *
   * @author lvweijie
   * @date 2023/12/1 12:42
   * @param key    key，加个前缀做区分
   * @param timeout timeout  单位秒
   * @param runnable  runnable
   */
  public void idempotent(String key, int timeout, CheckedRunnable runnable) {
    idempotent(key, timeout, TimeUnit.SECONDS, runnable);
  }

  /**
   * 幂等 （方法级，编程式的幂等方法）
   *
   * @author lvweijie
   * @date 2023/12/1 12:42
   * @param key    key，加个前缀做区分
   * @param timeout timeout
   * @param timeUnit timeUnit
   * @param runnable  runnable
   */
  public void idempotent(String key, int timeout, TimeUnit timeUnit, CheckedRunnable runnable) {
    String cacheKey = IDEMPOTENT_KEY_PREFIX + key;
    boolean result = setNx(cacheKey, 1, timeout, timeUnit);
    Assert.isTrue(result, BaseErrorEnum.IDEMPOTENT_ERROR, formatTtl(ttl(cacheKey)));
    try {
      runnable.run();
    } catch (Throwable e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 幂等 （方法级，编程式的幂等方法）
   *
   * @author lvweijie
   * @date 2023/12/1 12:42
   * @param key    key，加个前缀做区分
   * @param timeout timeout 单位秒
   * @param supplier  supplier
   */
  public <T> T idempotent(String key, int timeout, CheckedSupplier<T> supplier) {
    return idempotent(key, timeout, TimeUnit.SECONDS, supplier);
  }

  /**
   * 幂等 （方法级，编程式的幂等方法）
   *
   * @author lvweijie
   * @date 2023/12/1 12:42
   * @param key    key，加个前缀做区分
   * @param timeout timeout
   * @param timeUnit timeUnit
   * @param supplier  supplier
   */
  public <T> T idempotent(String key, int timeout, TimeUnit timeUnit, CheckedSupplier<T> supplier) {
    String cacheKey = IDEMPOTENT_KEY_PREFIX + key;
    boolean result = setNx(cacheKey, 1, timeout, timeUnit);
    Assert.isTrue(result, BaseErrorEnum.IDEMPOTENT_ERROR, formatTtl(ttl(cacheKey)));
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw Exceptions.unchecked(e);
    }
  }

  private static String formatTtl(long seconds) {
    if (seconds < 0) return "稍后";
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long remainingSeconds = seconds % 60;
    StringBuilder sb = new StringBuilder();
    if (hours > 0) {
      sb.append(hours).append("小时");
    }
    if (minutes > 0) {
      sb.append(minutes).append("分钟");
    }
    if (remainingSeconds > 0) {
      sb.append(remainingSeconds).append("秒钟");
    }
    sb.append("后");
    return sb.toString();
  }

  //-------------- rateLimit -----------

  /**
   * 限流
   *
   * @author lvweijie
   * @date 2023/12/1 15:32
   * @param key key 最好加前缀来区分
   * @param max 最大请求次数
   * @param period 时间周期(秒)
   * @param supplier supplier
   * @return T
   */
  public <T> T rateLimit(String key, long max, long period, CheckedSupplier<T> supplier) {
    return rateLimit(key, max, period, TimeUnit.SECONDS, supplier);
  }

  /**
   * 限流
   *
   * @author lvweijie
   * @date 2023/12/1 15:34
   * @param key key 最好加前缀来区分
   * @param max  最大请求次数
   * @param period 时间周期
   * @param timeUnit 时间周期单位
   * @param supplier  supplier
   * @return T
   */
  public <T> T rateLimit(String key, long max, long period, TimeUnit timeUnit, CheckedSupplier<T> supplier) {
    boolean allowed = isAllowed(key, max, period, timeUnit);
    Assert.isTrue(allowed, BaseErrorEnum.RATE_LIMIT_ERROR, max, timeUnit.toSeconds(period));
    try {
      return supplier.get();
    } catch (Throwable e) {
      throw Exceptions.unchecked(e);
    }
  }

  /**
   * 限流
   *
   * @author lvweijie
   * @date 2023/12/1 15:35
   * @param key key 最好加前缀来区分
   * @param max 最大请求次数
   * @param period 时间周期(秒)
   * @param runnable  runnable
   */
  public void rateLimit(String key, long max, long period, CheckedRunnable runnable) {
    rateLimit(key, max, period, TimeUnit.SECONDS, runnable);
  }

  /**
   * 限流
   *
   * @author lvweijie
   * @date 2023/12/1 15:36
   * @param key  key 最好加前缀来区分
   * @param max  最大请求次数
   * @param period 时间周期
   * @param timeUnit 时间周期单位
   * @param runnable  runnable
   */
  public void rateLimit(String key, long max, long period, TimeUnit timeUnit, CheckedRunnable runnable) {
    boolean allowed = isAllowed(key, max, period, timeUnit);
    Assert.isTrue(allowed, BaseErrorEnum.RATE_LIMIT_ERROR, max, timeUnit.toSeconds(period));
    try {
      runnable.run();
    } catch (Throwable e) {
      throw Exceptions.unchecked(e);
    }
  }

  private boolean isAllowed(String key, long max, long period, TimeUnit timeUnit) {
    String redisKey = RATE_LIMIT_KEY_PREFIX + key;
    List<String> keys = Collections.singletonList(redisKey);
    // 毫秒，考虑主从策略和脚本回放机制，这个time由客户端获取传入
    long now = System.currentTimeMillis();
    // 转为毫秒
    long ttlMillis = timeUnit.toMillis(period);
    // 执行命令
    Long result = execute(this.rateLimiterScript, keys, max, ttlMillis, now);
    return null != result && result != 0;
  }
}
