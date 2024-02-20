package com.kun.service;

import com.kun.entity.User;

import java.util.concurrent.ExecutionException;

public interface UserService {
    //用户注册
    boolean register(User user);
    //用户密码登录
    boolean loginByPassword(User user) throws ExecutionException, InterruptedException;
    //手机验证码登录
    void sendCode(String telephone);
    boolean checkCode(String tele,String code);
    User getUserById(int userId);
}
