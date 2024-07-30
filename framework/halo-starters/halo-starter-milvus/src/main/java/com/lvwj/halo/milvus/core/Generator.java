package com.lvwj.halo.milvus.core;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Generator {

    static List<String> generateEmptyScalars(int size) {
        String[] arr = new String[size];
        Arrays.fill(arr, "");

        return Arrays.asList(arr);
    }

    static List<JSONObject> generateEmptyJsons(int size) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new JSONObject(new HashMap<>()));
        }
        return list;
    }
}
