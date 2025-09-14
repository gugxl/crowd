package com.gugu.common.dao;

import java.util.List;
import java.util.Map;

public interface HiveRepository {
    List<Map<String, Object>> executeQuery(String sql);
    void executeUpdate(String sql);
}
