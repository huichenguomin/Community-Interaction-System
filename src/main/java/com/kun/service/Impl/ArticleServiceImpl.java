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
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper,Article> implements ArticleService {

    private static final Integer pageSize=1;
    @Autowired
    private ArticleMapper articleMapper;


    // 这样设计一个全局的wrapper会导致一个后果，每次做完一个操作都要去清空其中的条件
    // 并且wrapper的清空时机也需要把控
    LambdaQueryWrapper<Article> wrapper;

    public ArticleServiceImpl(){
        wrapper = new LambdaQueryWrapper<>();
    }
    @Override
    public List<Article> getAllArticles() {
        return articleMapper.selectList(null);
    }

    @Override
    public List<Article> getArticlesByPageNumOrderByViewNum(Integer pageNum) {
        Page<Article> page =new Page<>(pageNum,pageSize);
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // order by view_num desc

        wrapper.orderByDesc(Article::getViewNum);
        page(page,wrapper);
        wrapper.clear();
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
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle,words).or().like(Article::getSummary,words).or().like(Article::getContent,words).orderByDesc(Article::getViewNum);
        try{
            List <Article> list = articleMapper.selectList(wrapper);
            wrapper.clear();
            return new ResponseResult<>(StateCodeEnum.QUERY_ARTICLE_SUCCESS.getCode(),StateCodeEnum.QUERY_ARTICLE_SUCCESS.getMsg(), list);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        wrapper.clear();
        return new ResponseResult<>(StateCodeEnum.QUERY_ARTICLE_FAIL.getCode(), StateCodeEnum.QUERY_ARTICLE_FAIL.getMsg(), null);
    }

    @Override
    public ResponseResult<Boolean> incViewNum(Integer articleId) {
        Article article = this.getById(articleId);
        article.setViewNum(article.getViewNum()+1);
        boolean effectRow = this.updateById(article);
        if(effectRow) {
            return new ResponseResult<>(StateCodeEnum.INC_VIEW_NUM_SUCCESS.getCode(),StateCodeEnum.INC_VIEW_NUM_SUCCESS.getMsg(), effectRow);
        }
        return new ResponseResult<>(StateCodeEnum.INC_VIEW_NUM_FAIL.getCode(), StateCodeEnum.INC_VIEW_NUM_FAIL.getMsg(), effectRow);
    }

}
