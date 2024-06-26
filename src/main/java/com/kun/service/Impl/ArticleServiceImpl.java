package com.kun.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.dao.ArticleMapper;
import com.kun.dao.CategoryMapper;
import com.kun.entity.Article;
import com.kun.entity.Category;
import com.kun.entity.ResponseResult;
import com.kun.service.ArticleService;
import org.apache.ibatis.session.SqlSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings(value={"rawtypes","unchecked"})
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper,Article> implements ArticleService {

    private static final Integer pageSize=2;
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
    public ResponseResult<List<Article>> getArticlesByPageNumOrderByViewNum(Integer pageNum) {
        Page<Article> page =new Page<>(pageNum,pageSize);
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // order by view_num desc

        wrapper.orderByDesc(Article::getViewNum);
        page(page,wrapper);

        List<Article> list = null;
        try{
            list = page.getRecords();
        }catch (SqlSessionException e){
            e.printStackTrace();
        }finally {
            wrapper.clear();
        }
//        list.stream().map(article -> {
//            // 对查询到的文章进行批处理
//            return article;
//        }).collect(Collectors.toList());

        return (list==null) ? new ResponseResult<>(StateCodeEnum.GET_ORDERED_FAIL.getCode(), StateCodeEnum.GET_ORDERED_FAIL.getMsg(), list)
                            : new ResponseResult<>(StateCodeEnum.GET_ORDERED_SUCCESS.getCode(), StateCodeEnum.GET_ORDERED_SUCCESS.getMsg(), list);
    }

    @Override
    public ResponseResult<List<Article>> getArticlesByPageNumOrderByTime(Integer pageNum) {
        Page<Article> page = new Page<>(pageNum,pageSize);
        wrapper.orderByDesc(Article::getPunishTime);
        page(page,wrapper);
        List<Article> list = null;
        try {
            list = page.getRecords();
        }catch (SqlSessionException e){
            e.printStackTrace();
        }finally {
            wrapper.clear();
        }

        return (list==null) ? new ResponseResult<>(StateCodeEnum.GET_ORDERED_FAIL.getCode(), StateCodeEnum.GET_ORDERED_FAIL.getMsg(), list)
                : new ResponseResult<>(StateCodeEnum.GET_ORDERED_SUCCESS.getCode(), StateCodeEnum.GET_ORDERED_SUCCESS.getMsg(), list);
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
        Double score = redisTemplate.opsForZSet().score("hot",words);
        if(score==null)
            redisTemplate.opsForZSet().add("hot",words,1);
        else
            redisTemplate.opsForZSet().incrementScore("hot",words,1);

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
    /*
     * synchronized
     *
     */
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

    /**
     * attention: 这里考虑到可能执行sql时，发生异常，导致wrapper中的内容无法被释放而影响其他的语句
     *            所以将clear语句放到了finally中
     * @param name
     * @return
     */
    @Override
    public ResponseResult<List<Article>> getArticleByCateOrderByViewNum(String name) {
        LambdaQueryWrapper<Category> cateWrapper = new LambdaQueryWrapper<>();
        cateWrapper.eq(Category::getName,name);
        Category category = categoryMapper.selectOne(cateWrapper);


        wrapper.eq(Article::getCategory,category.getId()).orderByDesc(Article::getViewNum);
        List<Article> res = null;
        try{
            res = articleMapper.selectList(wrapper);
        }catch (SqlSessionException e) {
            e.printStackTrace();
        }finally {
            wrapper.clear();
        }
        return  (res==null) ? new ResponseResult<>(StateCodeEnum.GET_BY_CATEGORY_FAIL.getCode(), StateCodeEnum.PUBLISH_COMMENT_FAIL.getMsg(), res)
                            : new ResponseResult<>(StateCodeEnum.GET_BY_CATEGORY_SUCCESS.getCode(), StateCodeEnum.GET_BY_CATEGORY_SUCCESS.getMsg(), res);
    }

    @Override
    public ResponseResult<List<Article>> getArticleByCateOrderByTime(String name) {
        LambdaQueryWrapper<Category> cateWrapper = new LambdaQueryWrapper<>();
        cateWrapper.eq(Category::getName,name);
        Category category = categoryMapper.selectOne(cateWrapper);

        wrapper.eq(Article::getCategory,category.getId()).orderByDesc(Article::getPunishTime);
        List<Article> res = null;
        try{
            res = articleMapper.selectList(wrapper);
        }catch (SqlSessionException e) {
            e.printStackTrace();
        }finally {
            wrapper.clear();
        }
        return  (res==null) ? new ResponseResult<>(StateCodeEnum.GET_BY_CATEGORY_FAIL.getCode(), StateCodeEnum.PUBLISH_COMMENT_FAIL.getMsg(), res)
                : new ResponseResult<>(StateCodeEnum.GET_BY_CATEGORY_SUCCESS.getCode(), StateCodeEnum.GET_BY_CATEGORY_SUCCESS.getMsg(), res);
    }

    @Override
    public ResponseResult<Set> getHotWordsTopN(Integer n) {
        Set s = null;
        try{
            s = redisTemplate.opsForZSet().reverseRangeWithScores("hot",0,n);
        }catch (Exception e){
            e.printStackTrace();
        }

        return (s==null) ? new ResponseResult<>(StateCodeEnum.GET_HOT_WORDS_TOP_FAIL.getCode(), StateCodeEnum.GET_HOT_WORDS_TOP_FAIL.getMsg(), s)
                         : new ResponseResult<>(StateCodeEnum.GET_HOT_WORDS_TOP_SUCCESS.getCode(), StateCodeEnum.GET_HOT_WORDS_TOP_SUCCESS.getMsg(), s);
    }
}
