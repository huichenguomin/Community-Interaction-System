package com.kun.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.kun.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtils {

    public static String getToken(String userId,String sign){
        return JWT.create().withAudience(userId) //将userid保存到token的载荷Payload中
                .withExpiresAt(new Date(System.currentTimeMillis()+20*60*1000)) //设置token过期时间:20min
                .sign(Algorithm.HMAC256(sign)); //这里用用户的密码sign作为token的密钥
    }

}
