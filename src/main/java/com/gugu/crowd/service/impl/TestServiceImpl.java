package com.gugu.crowd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugu.asset.dao.HiveRepository;
import com.gugu.crowd.mapper.UserMapper;
import com.gugu.crowd.model.Users;
import com.gugu.crowd.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestServiceImpl extends ServiceImpl<UserMapper, Users> implements TestService {
    @Autowired
    private HiveRepository hiveRepository;

    @Override
    public List<Users> testDb() {
        return list();
    }

    @Override
    public Boolean testHive() {
        return hiveRepository.createTable("user_log");
    }
}
