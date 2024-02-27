package com.kun.service;

import com.kun.entity.Article;
import com.kun.entity.ResponseResult;

import java.util.List;

public interface ArticleService {
    // 查询所有文章
    List<Article> getAllArticles();
    // 用户发布一个帖子文章
    ResponseResult<String> postArticle(Article article);
    // 用户删除一个帖子
    ResponseResult<String> deleteArticle(Integer article_id);
    // 用户更新一个帖子
    ResponseResult<String> updateArticle(Article article);
    // 动态sql查询一个帖子的关键词
    ResponseResult<Article> queryArticle(Article article);

    ///  留言评论功能
    ///
}
