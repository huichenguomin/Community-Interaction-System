package com.kun.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum StateCodeEnum {
    GET_HOT_WORDS_TOP_SUCCESS(100,"get top hot words success"),
    GET_HOT_WORDS_TOP_FAIL(101,"get top hot words failed"),


    GET_ALL_ARTICLE_SUCCESS(500,"get all article success"),
    GET_ALL_ARTICLE_FAIL(501,"get failed!"),
    SAVE_OR_UPDATE_ARTICLE_SUCCESS(510,"save or update article success"),
    SAVE_OR_UPDATE_ARTICLE_FAIL(511,"save or update article fail"),
    DELETE_ARTICLE_SUCCESS(520,"delete the article success"),
    DELETE_ARTICLE_FAIL(520,"delete the article fail"),
    QUERY_ARTICLE_SUCCESS(530,"query articles by keywords success"),
    QUERY_ARTICLE_FAIL(531,"query articles by keywords fail"),
    INC_VIEW_NUM_SUCCESS(540,"increase view number success"),
    INC_VIEW_NUM_FAIL(541,"increase view number failed"),
    GET_BY_CATEGORY_SUCCESS(550,"get articles by category success"),
    GET_BY_CATEGORY_FAIL(551,"get articles by category failed"),
    GET_ORDERED_SUCCESS(560,"get ordered articles success"),
    GET_ORDERED_FAIL(561,"get ordered articles failed"),

    GET_COMMENTS_SUCCESS(600,"get comments by articleId success"),
    GET_COMMENTS_FAIL(601,"get comments fail"),
    PUBLISH_COMMENT_SUCCESS(610,"publish comment success"),
    PUBLISH_COMMENT_FAIL(611,"publish comment fail"),

    USER_LOGIN_SUCCESS(200,"login success"),
    USER_LOGIN_FAIL(201,"login failed!"),
    USER_REGISTER_SUCCESS(210,"register success"),
    USER_REGISTER_FAIL(211,"register failed！"),
    USER_UPDATE_SUCCESS(220,"update user success"),
    USER_UPDATE_FAIL(221,"update user failed"),
    GET_USER_INFO_SUCCESS(400,"get user info success"),
    GET_USER_INFO_FAIL(401,"get user info failed"),

    NO_EXIST_TELEPHONE(902,"there has no user by telephone given"),
    SEND_SMSCODE_SUCCESS(900,"send smscode success"),
    SEND_SMSCODE_FAIL(901,"send smscode failed"),
    LOGIN_BY_SMSCODE_SUCCESS(910,"login by smscode success"),
    LOGIN_BY_SMSCODE_FAIL(911,"check code error"),
    ;


    private final Integer code;
    private final String msg;
}
