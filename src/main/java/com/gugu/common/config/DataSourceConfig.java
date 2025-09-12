package com.gugu.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // Hive 数据源
    @Bean
    @ConfigurationProperties(prefix = "hive.datasource")
    public DataSource hiveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public JdbcTemplate hiveJdbcTemplate(DataSource hiveDataSource) {
        return new JdbcTemplate(hiveDataSource);
    }
}