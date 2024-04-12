package com.kun.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.dao.ArticleMapper;
import com.kun.entity.Article;
import com.kun.entity.ResponseResult;
import com.kun.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper,Article> implements ArticleService {

    private static final Integer pageSize=1;
    @Autowired
    private ArticleMapper articleMapper;
    @Override
    public List<Article> getAllArticles() {
        return articleMapper.selectList(null);
    }

    @Override
    public List<Article> getArticlesByPageNum(Integer pageNum) {
        Page<Article> page =new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 对于文章的返回有一定的限制条件
        page(page,null);

        List<Article> list = page.getRecords();
        list.stream().map(article -> {
            // 对查询到的文章进行批处理
            return article;
        }).collect(Collectors.toList());
        return list;
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
