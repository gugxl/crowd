package com.gugu.common.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HBaseConfig {

    @Value("${hbase.zookeeper-quorum}")
    private String zkQuorum;

    @Value("${hbase.client-port}")
    private String clientPort;

    @Bean
    public Connection hbaseConnection() throws IOException {
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zkQuorum);
        config.set("hbase.zookeeper.property.clientPort", clientPort);
        return ConnectionFactory.createConnection(config);
    }
}
