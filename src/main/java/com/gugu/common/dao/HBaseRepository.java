package com.gugu.common.dao;

public interface HBaseRepository {
    void put(String tableName, String rowKey, String cf, String qualifier, String value);
    String get(String tableName, String rowKey, String cf, String qualifier);
}
