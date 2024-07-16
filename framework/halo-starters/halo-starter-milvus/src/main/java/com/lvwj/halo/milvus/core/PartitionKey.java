package com.lvwj.halo.milvus.core;

import io.milvus.grpc.DataType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lvweijie
 * @date 2024年07月15日 21:17
 */
@Data
@NoArgsConstructor
public class PartitionKey implements Serializable {

    private String fieldName = "userId";
    private DataType dataType = DataType.Int64;
}
