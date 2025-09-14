package com.gugu.crowd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gugu.crowd.mapper.UserMapper;
import com.gugu.crowd.model.Users;
import com.gugu.crowd.service.TestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl extends ServiceImpl<UserMapper, Users> implements TestService {

    @Override
    public List<Users> testDb() {
        return list();
    }

    @Override
    public Boolean testHive() {
        return true;
    }

    @Override
    public Boolean testHbase() {

        return true;
    }
}
