package com.lvwj.halo.milvus.core;


import org.testcontainers.shaded.com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author lvweijie
 * @date 2024年07月30日 12:46
 */
public interface CollectionFieldConstant {

    String ID_FIELD_NAME = "id";
    String TEXT_FIELD_NAME = "text";
    String METADATA_FIELD_NAME = "metadata";
    String VECTOR_FIELD_NAME = "vector";
    String DELETE_FIELD_NAME = "deleted";

    Set<String> KEYS = Sets.newHashSet(ID_FIELD_NAME, TEXT_FIELD_NAME, VECTOR_FIELD_NAME, DELETE_FIELD_NAME);
}
