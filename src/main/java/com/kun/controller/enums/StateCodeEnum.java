package com.kun.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum StateCodeEnum {
    GET_ALL_ARTICLE_SUCCESS(200,"get all article success"),
    GET_ALL_ARTICLE_FAIL(501,"get failed!")
    ;


    private final Integer code;
    private final String msg;
}
