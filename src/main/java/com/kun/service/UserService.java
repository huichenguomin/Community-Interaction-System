package com.kun.service;

import com.kun.entity.User;

import java.util.concurrent.ExecutionException;

public interface UserService {
    //用户注册
    public boolean register(User user);
    //用户密码登录
    public boolean loginByPassword(User user) throws ExecutionException, InterruptedException;
    //手机验证码登录
    public void sendCode(String telephone);
    public boolean checkCode(String tele,String code);
}
