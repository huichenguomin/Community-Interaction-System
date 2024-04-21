package com.kun.service;

import com.kun.entity.Article;
import com.kun.entity.ResponseResult;

import java.net.Inet4Address;
import java.util.List;

public interface ArticleService {
    // 查询所有文章
    List<Article> getAllArticles();
    // 分页查询，热度最高的排前面
    // 后续考虑根据用户之前点击的内容采用对应的推荐算法
    List<Article> getArticlesByPageNumOrderByViewNum(Integer pageNum);
    // 用户发布或者更新一个帖子文章
    ResponseResult<Boolean> saveOrUpdateArticle(Article article);
    // 用户删除一个帖子
    ResponseResult<Boolean> deleteArticle(Integer article_id);

    // 匹配查询一个帖子的title -> summary ->content
    ResponseResult<List<Article>> queryArticleByWords(String words);

    //

    ///  留言评论功能
    ///
}
