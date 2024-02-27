package com.kun.service.Impl;

import com.kun.dao.ArticleMapper;
import com.kun.entity.Article;
import com.kun.entity.ResponseResult;
import com.kun.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public List<Article> getAllArticles() {
        return articleMapper.selectList(null);
    }

    @Override
    public ResponseResult<String> postArticle(Article article) {
        return null;
    }

    @Override
    public ResponseResult<String> deleteArticle(Integer article_id) {
        return null;
    }

    @Override
    public ResponseResult<String> updateArticle(Article article) {
        return null;
    }

    @Override
    public ResponseResult<Article> queryArticle(Article article) {
        return null;
    }
}
