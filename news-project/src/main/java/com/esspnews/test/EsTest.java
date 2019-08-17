package com.esspnews.test;

import com.esspnews.utils.EsUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class EsTest {
    public static void main(String[] args) {
        // 创建索引
        EsUtils.createIndex("spnews",3,0);
        // 设置mapping
    }
}
