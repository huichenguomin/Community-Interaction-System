package com.kun.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kun.Utils.HttpUtils;
import com.kun.dao.UserMapper;
import com.kun.entity.User;
import com.kun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    //redis模板
    @Autowired
    private StringRedisTemplate redisTemplate;
    //线程池
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(12);

    @Override
    public boolean register(User user) {
        //传入的对象中不包含uid，由系统生成
        //这里判断是否存在重复用户名
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //eq(数据库中的列，需要比较的值)
        wrapper.eq(User::getUsername,user.getUsername());
        User isRepeatUserName = userMapper.selectOne(wrapper);
        if(isRepeatUserName!=null) return false;
        else{
            userMapper.insert(user);
            return true;
        }
    }

    //这里的用户名是否需要缓存，安全否？需要开个线程？
    @Override
    public boolean loginByPassword(User user) throws ExecutionException, InterruptedException {
        //通过输入的用户名，查询数据库中的密码
        Callable callable = new Callable() {
            @Override
            public String call() throws Exception {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername,user.getUsername());
                try {
                    return userMapper.selectOne(wrapper).getPassword();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        Future<String> future = executor.submit(callable);
        while(true){
            if(future.isDone()) {
                //future的get方法若没获取到返回值则会一直阻塞
                try{
                    return user.getPassword().equals(future.get());
                }catch (NullPointerException e){
                    return false;
                }

            }
        }
    }
    @Override
    public User getUserById(int userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUid,userId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void sendCode(String telephone) {
            String code =String.valueOf((int)((Math.random()*9+1) * Math.pow(10,5)));
            //以电话号码为key，将验证码存入缓存中
            redisTemplate.opsForValue().set(telephone,code);

            String host = "https://gyytz.market.alicloudapi.com";
            String path = "/sms/smsSend";
            String method = "POST";
            String appcode = "03a80aa34e4d4269a35300aeab03c469";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", telephone);
            String msg = "**code**:"+code+",**minute**:5";
            querys.put("param", msg);

//smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html

            querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
            querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
            Map<String, String> bodys = new HashMap<String, String>();
//            bodys.put("content","验证码为："+code);
//            bodys.put("phone_number",telephone);
//            bodys.put("template_id", "TPL_0000");
            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t    \t* 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpUtils.doPost(host, path, method, headers, querys, bodys);
//                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    @Override
    public boolean checkCode(String telephone,String code) {
        return code.equals(redisTemplate.opsForValue().get(telephone));
    }


}
