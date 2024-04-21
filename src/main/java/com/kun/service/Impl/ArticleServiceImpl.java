package com.kun.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.dao.ArticleMapper;
import com.kun.entity.Article;
import com.kun.entity.ResponseResult;
import com.kun.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Article> getArticlesByPageNumOrderByViewNum(Integer pageNum) {
        Page<Article> page =new Page<>(pageNum,pageSize);
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // order by view_num desc
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getViewNum);
        page(page,wrapper);

        List<Article> list = page.getRecords();
        list.stream().map(article -> {
            // 对查询到的文章进行批处理
            return article;
        }).collect(Collectors.toList());
        return list;
    }

    @Transactional
    @Override
    public ResponseResult<Boolean> saveOrUpdateArticle(Article article) {
        boolean isDone = this.saveOrUpdate(article);
        if(isDone)
            return new ResponseResult<>(StateCodeEnum.SAVE_OR_UPDATE_ARTICLE_SUCCESS.getCode(), StateCodeEnum.SAVE_OR_UPDATE_ARTICLE_SUCCESS.getMsg(), null);
        return new ResponseResult<>(StateCodeEnum.SAVE_OR_UPDATE_ARTICLE_FAIL.getCode(), StateCodeEnum.SAVE_OR_UPDATE_ARTICLE_FAIL.getMsg(), null);
    }

    @Override
    public ResponseResult<Boolean> deleteArticle(Integer article_id) {
        boolean isDone = this.removeById(article_id);
        if(isDone)
            return new ResponseResult<>(StateCodeEnum.DELETE_ARTICLE_SUCCESS.getCode(),StateCodeEnum.DELETE_ARTICLE_SUCCESS.getMsg(), null);
        return new ResponseResult<>(StateCodeEnum.DELETE_ARTICLE_FAIL.getCode(),StateCodeEnum.DELETE_ARTICLE_FAIL.getMsg(), null);
    }


    // match to title -> summary ->content
    // need to query page
    @Override
    public ResponseResult<List<Article>> queryArticleByWords(String words) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle,words).or().like(Article::getSummary,words).or().like(Article::getContent,words).orderByDesc(Article::getViewNum);
        try{
            List <Article> list = articleMapper.selectList(wrapper);
            return new ResponseResult<>(StateCodeEnum.QUERY_ARTICLE_SUCCESS.getCode(),StateCodeEnum.QUERY_ARTICLE_SUCCESS.getMsg(), list);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return new ResponseResult<>(StateCodeEnum.QUERY_ARTICLE_FAIL.getCode(), StateCodeEnum.QUERY_ARTICLE_FAIL.getMsg(), null);
    }

}
