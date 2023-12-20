package com.ww.dao;

import com.ww.entity.User;
import com.ww.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginDao {

    @Autowired
    private UserMapper userMapper;

    public Integer login(User user){
        return userMapper.login(user);
    }
}
