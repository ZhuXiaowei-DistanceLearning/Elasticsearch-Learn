package com.zxw.docu;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class TextDocument {
    public static String CLUSTER_NAME = "elasticsearch";
    public static String HOST_IP = "127.0.0.1";
    public static int TCP_PORT = 9300;

    public static void main(String[] args) throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
        // 新建文档
//        createDoc(client);
        GetResponse response = client.prepareGet("books", "_search",null).get();
        System.out.println(response.getSourceAsString());
    }

    private static void createDoc(TransportClient client) {
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("user", "kumchy");
        doc2.put("postDate", "2013-01-30");
        doc2.put("message", "trying out Elasticsearch");
        IndexResponse response = client.prepareIndex("books","it").setSource(doc2).get();
        System.out.println(response.status());
    }
}
