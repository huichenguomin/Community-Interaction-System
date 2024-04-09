package com.kun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.dao.UserMapper;
import com.kun.entity.ResponseResult;
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
    @Autowired
    private UserMapper userMapper;
    /*
        用户注册
        这里数据库自增uid太大，需要去设置自增的起始值和步长
     */
    @PostMapping("/register")
    public ResponseResult<User> register(@RequestBody User user){
        if(service.register(user)){
            System.out.println("注册成功！");
            return new ResponseResult<>(StateCodeEnum.USER_REGISTER_SUCCESS.getCode(), StateCodeEnum.USER_REGISTER_SUCCESS.getMsg(), null);
        }
        return new ResponseResult<>(StateCodeEnum.USER_REGISTER_FAIL.getCode(), StateCodeEnum.USER_REGISTER_FAIL.getMsg(), null);
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
    public ResponseResult<User> loginByPassword(@RequestBody User user) throws ExecutionException, InterruptedException {
        if(service.loginByPassword(user)){
            System.out.println("用户"+user.getUsername()+"欢迎您！");

            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getUsername,user.getUsername());
            User userInfo = userMapper.selectOne(lambdaQueryWrapper);
            if(userInfo!=null) userInfo.setPassword(null);
            return new ResponseResult<>(StateCodeEnum.USER_LOGIN_SUCCESS.getCode(), StateCodeEnum.USER_LOGIN_SUCCESS.getMsg(), userInfo);
        }
        return new ResponseResult<>(StateCodeEnum.USER_LOGIN_FAIL.getCode(), StateCodeEnum.USER_LOGIN_FAIL.getMsg(), null);
    }

    @GetMapping("/getUserInfo")
    public ResponseResult<User> getUser(User user){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(user!=null) {
            wrapper.eq(User::getUsername,user.getUsername());
            return new ResponseResult<>(StateCodeEnum.GET_USER_INFO_SUCCESS.getCode(), StateCodeEnum.GET_USER_INFO_SUCCESS.getMsg(), userMapper.selectOne(wrapper));
        }
        return new ResponseResult<>(StateCodeEnum.GET_USER_INFO_FAIL.getCode(), StateCodeEnum.GET_USER_INFO_FAIL.getMsg(), null);
    }

    // 更新用户信息(删除用户)需要设置事务，可回滚
    @PostMapping("/updateUserInfo")
    public ResponseResult<User> updateUser(@RequestBody User user){
        if(userMapper.updateById(user)!=0){
            return new ResponseResult<>(StateCodeEnum.USER_UPDATE_SUCCESS.getCode(), StateCodeEnum.USER_UPDATE_SUCCESS.getMsg(), userMapper.selectById(user.getUid()));
        }
        return new ResponseResult<>(StateCodeEnum.USER_UPDATE_FAIL.getCode(),StateCodeEnum.USER_UPDATE_FAIL.getMsg(),null);
    }
}
