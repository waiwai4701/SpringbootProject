package com.ww.controller;

import com.ww.entity.User;
import com.ww.dao.LoginDao;
import com.ww.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginDao loginService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/login")
    public String login(User user) {
        int i = 0;
        //先从redis缓存读取，如果不存在，再从mysql读取
        Object user_redis =  redisUtil.get(user.getUsername());
        if(user_redis != null){
            log.info("login via redis");
            return "success";
        }else{
            i = loginService.login(user);
        }

        if( i > 0){
            log.info("login via mysql");
            //将新登录成功的用户信息放入redis,失效时间单位为3分钟
            boolean setFlg = redisUtil.set(user.getUsername(),user, 60*3);
            log.info("login set user to redis："+setFlg);
            return "success";
        }else{
            return "fail";
        }
    }
}
