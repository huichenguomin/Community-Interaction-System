package com.kun.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum StateCodeEnum {
    GET_ALL_ARTICLE_SUCCESS(500,"get all article success"),
    GET_ALL_ARTICLE_FAIL(501,"get failed!"),

    USER_LOGIN_SUCCESS(200,"login success"),
    USER_LOGIN_FAIL(201,"login failed!"),
    USER_REGISTER_SUCCESS(210,"register success"),
    USER_REGISTER_FAIL(211,"register failed！"),
    USER_UPDATE_SUCCESS(220,"update user success"),
    USER_UPDATE_FAIL(221,"update user failed"),
    GET_USER_INFO_SUCCESS(400,"get user info success"),
    GET_USER_INFO_FAIL(401,"get user info failed")
    ;


    private final Integer code;
    private final String msg;
}
