package com.kun.dto;

import lombok.Data;

//这个DTO类用于接收 前端登录请求的参数
@Data
public class UserDTO {
    private Integer uid;
    private String username;
    private String password;
    private String telephone;

    private String token;
}
