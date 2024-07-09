package com.lvwj.halo.join.support;

import com.lvwj.halo.common.utils.CollectionUtil;
import com.lvwj.halo.core.node.BaseNode;
import com.lvwj.halo.join.JoinItemExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
abstract class AbstractJoinItemExecutor extends BaseNode<JoinItemExecutor> implements JoinItemExecutor {

    /**
     * 从原始数据中生成 JoinKey
     * @param data
     * @return
     */
    protected abstract Object createJoinKeyFromSourceData(Object data);

    /**
     * 根据 JoinKey 批量获取 JoinData
     * @param joinKeys
     * @return
     */
    protected abstract List<Object> getJoinDataByJoinKeys(List<Object> joinKeys);

    /**
     * 从 JoinData 中获取 JoinKey
     * @param joinData
     * @return
     */
    protected abstract Object createJoinKeyFromJoinData(Object joinData);

    /**
     * 将 JoinData 转换为 JoinResult
     * @param joinData
     * @return
     */
    protected abstract Object convertToResult(Object joinData);

    /**
     * 将 JoinResult 写回至 SourceData
     * @param data
     * @param JoinResults
     */
    protected abstract void onFound(Object data, List<Object> JoinResults);

    /**
     * 未找到对应的 JoinData
     * @param data
     * @param joinKey
     */
    protected abstract void onNotFound(Object data, Object joinKey);

    @Override
    public void execute(List<Object> sourceDatas) {
        if(CollectionUtil.isEmpty(sourceDatas)) return;

        // 从源数据中提取 JoinKey
        List<Object> joinKeys = sourceDatas.stream()
                .filter(Objects::nonNull)
                .map(this::createJoinKeyFromSourceData)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());
        log.debug("get join key {} from source data {}", joinKeys, sourceDatas);
        if(CollectionUtil.isEmpty(joinKeys)) return;

        // 根据 JoinKey 获取 JoinData
        List<Object> allJoinDatas = getJoinDataByJoinKeys(joinKeys);
        log.debug("get join data {} by join key {}", allJoinDatas, joinKeys);
        if(CollectionUtil.isEmpty(allJoinDatas)) return;

        // 将 JoinData 以 Map 形式进行组织
        Map<Object, List<Object>> joinDataMap = allJoinDatas.stream()
                .filter(Objects::nonNull)
                .collect(groupingBy(this::createJoinKeyFromJoinData));
        log.debug("group by join key, result is {}", joinDataMap);

        // 处理每一条 SourceData
        for (Object data : sourceDatas){
            // 从 SourceData 中 获取 JoinKey
            Object joinKey = createJoinKeyFromSourceData(data);
            if (joinKey == null){
                log.warn("join key from join data {} is null", data);
                continue;
            }
            // 根据 JoinKey 获取 JoinData
            List<Object> joinDatasByKey = joinDataMap.get(joinKey);
            if (CollectionUtil.isNotEmpty(joinDatasByKey)){
                // 获取到 JoinData， 转换为 JoinResult，进行数据写回
                List<Object> joinResults = joinDatasByKey.stream()
                        .filter(Objects::nonNull)
                        .map(this::convertToResult)
                        .collect(toList());

                log.debug("success to convert join data {} to join result {}", joinDatasByKey, joinResults);
                onFound(data, joinResults);
                log.debug("success to write join result {} to source data {}", joinResults, data);
            }else {
                log.warn("join data lost by join key {} for source data {}", joinKey, data);
                // 为获取到 JoinData，进行 notFound 回调
                onNotFound(data, joinKey);
            }
        }
    }
}
