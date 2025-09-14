package com.gugu.common.config;

import org.apache.hive.com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class HiveConfig {

    @Value("${hive.jdbc-url}")
    private String jdbcUrl;

    @Value("${hive.username}")
    private String username;

    @Value("${hive.password}")
    private String password;

    @Bean
    public DataSource hiveDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        ds.setMaximumPoolSize(10);
        return ds;
    }

    @Bean
    public JdbcTemplate hiveJdbcTemplate(@Autowired DataSource hiveDataSource) {
        return new JdbcTemplate(hiveDataSource);
    }
}
