package com.kun.controller;

import com.kun.entity.User;
import com.kun.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/cis")
public class UserController {
    @Autowired
    private UserServiceImpl service;
    /*
        用户注册
        这里数据库自增uid太大，需要去设置自增的起始值和步长
     */
    @PostMapping("/register")
    public void register(@RequestBody User user){
        if(service.register(user)){
            System.out.println("注册成功！");
        }else{
            System.out.println("注册失败");
        }
    }

    /*
        发送验证码到手机的api
     */
    @GetMapping("/{telephone}")
    public void sendCode(@PathVariable String telephone){
        service.sendCode(telephone);
    }
    /*
        输入验证码后进行验证，传给前端一个返回值（安全问题，避免伪造登录成功）
     */
    @PostMapping("/{telephone}/{code}")
    public boolean checkCode(@PathVariable String telephone,@PathVariable String code){
        return service.checkCode(telephone,code);
    }

    /*
        用户名密码登录
        这里暂且用json格式提交
     */
    @PostMapping("/login")
    public void loginByPassword(@RequestBody User user) throws ExecutionException, InterruptedException {
        if(service.loginByPassword(user)){
            System.out.println("用户"+user.getUsername()+"欢迎您！");
        }
        else{
            System.out.println("密码错误！");
        }
    }
}
