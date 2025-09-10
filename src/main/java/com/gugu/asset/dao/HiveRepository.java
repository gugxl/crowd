package com.gugu.asset.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class HiveRepository {
    private final JdbcTemplate hiveJdbcTemplate;

    public HiveRepository(@Qualifier("hiveJdbcTemplate") JdbcTemplate hiveJdbcTemplate) {
        this.hiveJdbcTemplate = hiveJdbcTemplate;
    }

    public List<Map<String, Object>> query(String sql) {
        return hiveJdbcTemplate.queryForList(sql);
    }

    public Boolean createTable(String tableName) {
        String sql = String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
                "                    id BIGINT,\n" +
                "                    name STRING\n" +
                "                )\n" +
                "                STORED AS PARQUET",tableName);

         hiveJdbcTemplate.execute(sql);
        return true;
    }

    public void dropTable() {
        hiveJdbcTemplate.execute("DROP TABLE IF EXISTS user_log");
    }
}