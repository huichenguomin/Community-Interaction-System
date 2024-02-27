package com.kun.controller;

import com.kun.controller.enums.StateCodeEnum;
import com.kun.entity.Article;
import com.kun.entity.ResponseResult;
import com.kun.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cis")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/all")
    public ResponseResult<List<Article>> allArticle(){
        List<Article> list = articleService.getAllArticles();
        if(list.isEmpty()){
            return new ResponseResult<>(StateCodeEnum.GET_ALL_ARTICLE_FAIL.getCode(),StateCodeEnum.GET_ALL_ARTICLE_FAIL.getMsg(),null );
        }
        return new ResponseResult<>(StateCodeEnum.GET_ALL_ARTICLE_SUCCESS.getCode(),StateCodeEnum.GET_ALL_ARTICLE_SUCCESS.getMsg(), list);
    }
}
