package com.tf.controller;

import com.tf.entity.User;
import com.tf.mapper.UserMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jason_moo on 2018/11/16.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/hello")
    @ResponseBody
    public Object sayHello(HttpServletRequest request){
        User user =userMapper.getById(1l);
        return user;
    }

}
