package com.kun.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.Utils.TokenUtils;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.dao.UserMapper;
import com.kun.entity.ResponseResult;
import com.kun.entity.User;
import com.kun.service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    @Autowired
    private TokenUtils tokenUtils;
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
    public ResponseResult<String> sendCode(@PathVariable String telephone){
        // 先根据手机号查询数据库是否存在这个用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTelephone,telephone);
        User user = service.getOne(wrapper);
        if(user!=null){
            try{
                service.sendCode(telephone);
                return new ResponseResult<>(StateCodeEnum.USER_UPDATE_SUCCESS.getCode(), StateCodeEnum.SEND_SMSCODE_SUCCESS.getMsg(), null);
            }catch (Exception e){
                e.printStackTrace();
                return new ResponseResult<>(StateCodeEnum.SEND_SMSCODE_FAIL.getCode(), StateCodeEnum.SEND_SMSCODE_FAIL.getMsg(), null);
            }
        }
        return new ResponseResult<>(StateCodeEnum.NO_EXIST_TELEPHONE.getCode(), StateCodeEnum.NO_EXIST_TELEPHONE.getMsg(), null);
    }
    /*
        输入验证码后进行验证，传给前端一个返回值（安全问题，避免伪造登录成功）
     */
//    @PostMapping("/{telephone}/{code}")
//    public boolean checkCode(@PathVariable String telephone,@PathVariable String code){
//        // 这里验证码通过之后应该返回一个token给前端
//        return service.checkCode(telephone,code);
//    }

    /*
        用户名密码登录
        这里暂且用json格式提交
     */
    @PostMapping("/loginByPwd")
    public ResponseResult<String> loginByPassword(@RequestBody User user, HttpServletResponse response) throws ExecutionException, InterruptedException {
        if(service.loginByPassword(user)){
            System.out.println("用户"+user.getUsername()+"欢迎您！");

//            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(User::getUsername,user.getUsername());
//            User userInfo = userMapper.selectOne(lambdaQueryWrapper);
//            if(userInfo!=null) userInfo.setPassword(null);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername,user.getUsername());
            User currUser = service.getOne(wrapper);
            String token = TokenUtils.getToken(String.valueOf(currUser.getUid()));
            response.setHeader("token",token);
            return new ResponseResult<>(StateCodeEnum.USER_LOGIN_SUCCESS.getCode(), StateCodeEnum.USER_LOGIN_SUCCESS.getMsg(), null);
        }
        return new ResponseResult<>(StateCodeEnum.USER_LOGIN_FAIL.getCode(), StateCodeEnum.USER_LOGIN_FAIL.getMsg(), null);
    }

    /*
        loginBySMScode
        先调用sendCode接口，然后将获取到的code和telephone一起返回回来，然后做一次根据telephone的查询，获取到用户后，将用户的token返回
     */
    @PostMapping("/{telephone}/{code}")
    public ResponseResult<String> loginBySMSCode(@PathVariable String telephone,@PathVariable String code,HttpServletResponse response){
        String returnMsg;
        if(service.checkCode(telephone, code)){
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getTelephone,telephone);
            User user = service.getOne(wrapper);
            // 响应头返回token
            response.setHeader("token",TokenUtils.getToken(String.valueOf(user.getUid())));
            return new ResponseResult<>(StateCodeEnum.LOGIN_BY_SMSCODE_SUCCESS.getCode(), StateCodeEnum.LOGIN_BY_SMSCODE_SUCCESS.getMsg(), null);
        }
        return new ResponseResult<>(StateCodeEnum.LOGIN_BY_SMSCODE_FAIL.getCode(), StateCodeEnum.LOGIN_BY_SMSCODE_FAIL.getMsg(), null);
    }

    @GetMapping("/getUserInfo")
    public ResponseResult<User> getUser(HttpServletRequest request){
        String token = request.getHeader("token");
        if(token!=null) {
            User user = tokenUtils.decodeTokenToUser(token);
            return new ResponseResult<>(StateCodeEnum.GET_USER_INFO_SUCCESS.getCode(), StateCodeEnum.GET_USER_INFO_SUCCESS.getMsg(), user);
        }
        return new ResponseResult<>(StateCodeEnum.GET_USER_INFO_FAIL.getCode(), StateCodeEnum.GET_USER_INFO_FAIL.getMsg(), null);
    }
    /*
      更新用户信息(删除用户)需要设置事务，可回滚
      这里拦截器会拦截到
    */
    @PostMapping("/updateUserInfo")
    public ResponseResult<User> updateUser(@RequestBody User user){
        if(userMapper.updateById(user)!=0){
            return new ResponseResult<>(StateCodeEnum.USER_UPDATE_SUCCESS.getCode(), StateCodeEnum.USER_UPDATE_SUCCESS.getMsg(), userMapper.selectById(user.getUid()));
        }
        return new ResponseResult<>(StateCodeEnum.USER_UPDATE_FAIL.getCode(),StateCodeEnum.USER_UPDATE_FAIL.getMsg(),null);
    }
}
