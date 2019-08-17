package com.esspnews.dao;

import com.esspnews.utils.EsUtils;
import org.elasticsearch.client.transport.TransportClient;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bee on 17/8/11.
 */
public class Dao {

    private Connection conn;


    public void getConnection(){


        try {
            Class.forName("com.mysql.jdbc.Driver");
            String user="root";
            String passwd="123456";
            String url="jdbc:mysql://localhost:3306/News";

            conn= DriverManager.getConnection(url,user,passwd);

            if (conn!=null){
                System.out.println("mysql连接成功!");
            }else{
                System.out.println("mysql连接失败!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mysqlToEs(){

        String sql="SELECT * FROM news";

        TransportClient client= EsUtils.getSingleClient();

        try {
            PreparedStatement pstm=conn.prepareStatement(sql);

            ResultSet resultSet=pstm.executeQuery();

            Map<String,Object> map=new HashMap<String, Object>();
            while (resultSet.next()){

                int nid=resultSet.getInt(1);

                map.put("id",nid);
                map.put("title",resultSet.getString(2));
                map.put("key_word",resultSet.getString(3));
                map.put("content",resultSet.getString(4));
                map.put("url",resultSet.getString(5));
                map.put("reply",resultSet.getInt(6));
                map.put("source",resultSet.getString(7));

                String postdatetime=resultSet.getTimestamp(8).toString();

                map.put("postdate",postdatetime.substring(0,postdatetime.length()-2));


                System.out.println(map);
                client.prepareIndex("spnews","news",String.valueOf(nid))
                        .setSource(map)
                        .execute()
                        .actionGet();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
