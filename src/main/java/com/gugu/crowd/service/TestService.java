package com.gugu.crowd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gugu.crowd.model.Users;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TestService extends IService<Users> {

    List<Users> testDb();

    Boolean testHive();
}
