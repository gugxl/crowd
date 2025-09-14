package com.gugu.common.dao.impl;

import com.gugu.common.dao.HiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class HiveRepositoryImpl implements HiveRepository {

    @Autowired
    private DataSource hiveDataSource; // HikariCP + Hive JDBC

    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        try (Connection conn = hiveDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Map<String, Object>> result = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeUpdate(String sql) {
        try (Connection conn = hiveDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
