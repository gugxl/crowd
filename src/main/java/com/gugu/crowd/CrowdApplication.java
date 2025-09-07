package com.gugu.crowd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gugu")
@MapperScan("com.gugu.crowd.mapper")

public class CrowdApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrowdApplication.class, args);
    }

}
