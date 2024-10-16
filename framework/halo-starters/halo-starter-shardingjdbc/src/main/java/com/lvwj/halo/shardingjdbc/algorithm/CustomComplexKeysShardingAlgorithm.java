package com.lvwj.halo.shardingjdbc.algorithm;

import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringPool;
import com.lvwj.halo.core.snowflake.SnowflakeUtil;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义复合分片算法
 *
 * @author lvweijie
 * @date 2024年10月14日 10:10
 */
public class CustomComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {

    private static final String SHARDING_COUNT = "sharding-count";
    private static final String SHARDING_KEY = "sharding-key";
    private static final String SHARDING_EXT_KEYS = "sharding-ext-keys";

    /**
     * 分片数
     */
    private Integer shardingCount;
    /**
     * 分片字段
     */
    private String shardingKey;
    /**
     * 扩展分片字段
     */
    private String shardingExtKeys;

    private Properties props = new Properties();

    @Override
    public String getType() {
        return "COMPLEX-Custom";
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties properties) {
        this.props = properties;
        this.shardingCount = Func.toInt(props.get(SHARDING_COUNT));
        if (null == shardingCount || this.shardingCount < 2 || shardingCount % 2 != 0) {
            throw new IllegalArgumentException("复合分片算法[" + getType() + "], 属性[" + SHARDING_COUNT + "]必须是2的倍数");
        }
        this.shardingKey = Func.toStr(props.get(SHARDING_KEY));
        if (Func.isBlank(this.shardingKey)) {
            throw new IllegalArgumentException("复合分片算法[" + getType() + "], 属性[" + SHARDING_KEY + "]不能为空");
        }
        this.shardingExtKeys = Func.toStr(props.get(SHARDING_EXT_KEYS));
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTableNames, ComplexKeysShardingValue<Comparable<?>> complexKeysShardingValue) {
        Set<String> set = new HashSet<>();
        String logicTableName = complexKeysShardingValue.getLogicTableName();
        Map<String, Collection<Comparable<?>>> map = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        if (map.containsKey(this.shardingKey)) {
            set.addAll(map.get(this.shardingKey).stream()
                    .map(v -> getActualTable(logicTableName, getIndex((Long) v)))
                    .filter(availableTableNames::contains)
                    .collect(Collectors.toSet()));
        }
        if (map.containsKey("id")) {
            set.addAll(map.get("id").stream()
                    .filter(s -> SnowflakeUtil.getShardValue((Long) s) != null)
                    .map(v -> getActualTable(logicTableName, getIndex(SnowflakeUtil.getShardValue((Long) v))))
                    .filter(availableTableNames::contains)
                    .collect(Collectors.toSet())
            );
        }
        if (StringUtils.hasText(shardingExtKeys)) {
            for (String field : shardingExtKeys.split(StringPool.COMMA)) {
                String extKey = field.trim();
                if (!map.containsKey(extKey)) {
                    continue;
                }
                set.addAll(map.get(extKey).stream()
                        .filter(s -> SnowflakeUtil.getShardValue((Long) s) != null)
                        .map(v -> getActualTable(logicTableName, getIndex(SnowflakeUtil.getShardValue((Long) v))))
                        .filter(availableTableNames::contains).collect(Collectors.toSet())
                );
            }
        }
        return set;
    }

    private long getIndex(Long tenantId) {
        return tenantId % shardingCount;
    }

    private String getActualTable(String logicTableName, long index) {
        return logicTableName + StringPool.UNDERSCORE + index;
    }
}
