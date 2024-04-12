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
    // 发布文章
    // 从token中获取发布者信息，与文章一同存入数据库

    // 点击文章标题进入阅读
    // 获取文章详细信息，阅读量+1

    // 修改文章
    // 修改时间

    // 删除文章
    // 再整一个表暂存删除的文章，指定时间内可以找回

    // 评论功能
}
