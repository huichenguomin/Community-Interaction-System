package com.kun.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kun.entity.User;
import com.kun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtils {
    @Autowired
    private UserService userService;

    private static final String sign = "hello %#& wk";
    public static String getToken(String userId){
        return JWT.create().withAudience(userId) //将userid保存到token的载荷Payload中
                .withExpiresAt(new Date(System.currentTimeMillis()+20*60*1000)) //设置token过期时间:20min
                .sign(Algorithm.HMAC256(sign)); //这里用用户的密码sign作为token的密钥
    }
    // 模仿JwtInterceptor添加解析token的功能
    public User decodeTokenToUser(String token){
//        String userid = JWT.decode(token).getAudience().get(0);
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(sign)).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String userid = decodedJWT.getClaim("aud").asString();
        return userService.getUserById(Integer.parseInt(userid));
    }

}
