package com.gugu.common.dao.impl;

import com.gugu.common.dao.HBaseRepository;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

@Service
public class HBaseRepositoryImpl implements HBaseRepository {

    @Autowired
    private Connection hbaseConnection; // HBase Connection

    @Override
    public void put(String tableName, String rowKey, String cf, String qualifier, String value) {
        try (Table table = hbaseConnection.getTable(TableName.valueOf(tableName))) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            table.put(put);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String tableName, String rowKey, String cf, String qualifier) {
        try (Table table = hbaseConnection.getTable(TableName.valueOf(tableName))) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(cf), Bytes.toBytes(qualifier));
            Result result = table.get(get);
            byte[] value = result.getValue(Bytes.toBytes(cf), Bytes.toBytes(qualifier));
            return value != null ? Bytes.toString(value) : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
