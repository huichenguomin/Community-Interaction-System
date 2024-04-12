package com.kun.config.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.kun.entity.User;
import com.kun.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //用户对某个连接申请访问
        //从拦截到的请求中获取请求头中的token
        String token = request.getHeader("token");
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        //认证
        if(StringUtil.isBlank(token)){
            //请求头中无token，抛出异常
            throw new Exception("无token");
        }
        String userId = null;
        try{
            //获取在用户登录时存入载荷中用户的id
            userId = JWT.decode(token).getAudience().get(0);
        }catch (JWTDecodeException e){
            //验证失败
            throw new Exception("验证失败");
        }

        //根据token获取到的userid查询数据库
        User user = userService.getUserById(Integer.parseInt(userId));
        if(user==null){
            //用户不存在
            throw new Exception("用户不存在");
        }
        //用户实际的密码作为密钥，去验证token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try{
            jwtVerifier.verify(token);
        }catch (JWTVerificationException e){
            throw new Exception("验证失败");
        }
        return true;
    }
}
