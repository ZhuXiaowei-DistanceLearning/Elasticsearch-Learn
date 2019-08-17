package com.zxw.index;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 索引API
 */
public class TextIndices {
    public static String CLUSTER_NAME = "elasticsearch";
    public static String HOST_IP = "127.0.0.1";
    public static int TCP_PORT = 9300;

    public static void main(String[] args) throws UnknownHostException {
        // 指定集群名称
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        //
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
        IndicesAdminClient indices = client.admin().indices();
        // 判断索引是否存在
        IndicesExistsResponse books = indices.prepareExists("books").get();
        System.out.println(books.isExists());
        // 判断type是否存在
        TypesExistsResponse response = indices.prepareTypesExists("books").setTypes("type1", "type2").get();
        System.out.println(response.isExists());
        // 创建索引
//        createIndex(indices);
        // 更新settings
//        updateSettings(settings, indices);
        // 获取settings
//        getSettings(settings, indices);
        // 设置mapping
        AcknowledgedResponse response1 = client.admin().indices().preparePutMapping("twitter").setType("tweet").setSource("{\"properties\":{\"name\":{" +
                "\"type\":\"keyword\"}}}").get();
        System.out.println(response1);
    }

    private static void createIndex(IndicesAdminClient indices) {
        indices.prepareCreate("twitter").setSettings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2)).get();
    }

    private static void updateSettings(Settings settings, IndicesAdminClient indices) {
        // 更新副本
        indices.prepareUpdateSettings("twitter").setSettings(Settings.builder().put("index.number_of_replicas", 0)).get();
    }

    private static void getSettings(Settings settings, IndicesAdminClient indices) {
        // 获取Settings
        GetSettingsResponse getSettingsResponse = indices.prepareGetSettings("twitter").get();
        for (ObjectObjectCursor<String, Settings> indexToSetting : getSettingsResponse.getIndexToSettings()) {
            String index = indexToSetting.key;
            Settings value = indexToSetting.value;
            Integer shards = settings.getAsInt("index.number_of_shards", null);
            Integer replicas = settings.getAsInt("index.number_of_replicas", null);
            System.out.println(index);
            System.out.println(value);
            System.out.println(shards);
            System.out.println(replicas);
        }
    }
}
