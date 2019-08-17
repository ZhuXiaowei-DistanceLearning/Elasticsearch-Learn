package com.zxw.Test;


import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class TestClient {
    public static String CLUSTER_NAME = "elasticsearch";
    public static String HOST_IP = "127.0.0.1";
    public static int TCP_PORT = 9300;

    public static void main(String[] args) throws UnknownHostException {
        // 指定集群名称
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        //
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
        // 读取文档
        GetResponse response = client.prepareGet("books", "IT", "1").get();
        System.out.println(response.getSourceAsString());
    }
}
