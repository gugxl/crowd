package com.gugu.asset.dao;

import lombok.SneakyThrows;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HbaseRepository {


    public Boolean testGet() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "localhost");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            TableName tableName = TableName.valueOf("user"); // 表名
            Table table = connection.getTable(tableName);

            // 插入数据
            Put put = new Put(Bytes.toBytes("row1")); // rowkey
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("Alice"));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("20"));
            table.put(put);

            // 查询数据
            Get get = new Get(Bytes.toBytes("row1"));
            Result result = table.get(get);
            byte[] name = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
            byte[] age = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("age"));
            System.out.println("Name: " + Bytes.toString(name) + ", Age: " + Bytes.toString(age));

            table.close();

            return true;
        }
    }
}
