package com.gugu.crowd.controller;

import com.gugu.crowd.model.Users;
import com.gugu.crowd.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@Tag(name = "测试类", description = "测试工具") // 添加控制器标签
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/testDb")
    @Operation(summary = "数据库连接测试", description = "业务数据库是否连接成功")
    public List<Users> testDb() {
        return testService.testDb();
    }

    @GetMapping("/testHive")
    @Operation(summary = "数据仓库hive尝试连接", description = "数据仓库hive尝试连接")
    public Boolean testHive() {
        return testService.testHive();
    }


    @GetMapping("/testHbase")
    @Operation(summary = "hbase尝试连接", description = "hbase尝试连接")
    public Boolean testHbase() {
        return testService.testHbase();
    }
}
