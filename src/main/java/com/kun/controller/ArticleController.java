package com.kun.controller;

import com.kun.Utils.TokenUtils;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.entity.Article;
import com.kun.entity.ResponseResult;
import com.kun.entity.User;
import com.kun.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

import java.util.List;


@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TokenUtils tokenUtils;

    @GetMapping("/all")
    public ResponseResult<List<Article>> allArticle(){
        List<Article> list = articleService.getAllArticles();
        if(list.isEmpty()){
            return new ResponseResult<>(StateCodeEnum.GET_ALL_ARTICLE_FAIL.getCode(),StateCodeEnum.GET_ALL_ARTICLE_FAIL.getMsg(),null );
        }
        return new ResponseResult<>(StateCodeEnum.GET_ALL_ARTICLE_SUCCESS.getCode(),StateCodeEnum.GET_ALL_ARTICLE_SUCCESS.getMsg(), list);
    }

    @GetMapping("/getByPageNum/{pageNum}")
    public ResponseResult<List<Article>> getArticlesByPageNum(@PathVariable Integer pageNum){
        return new ResponseResult<>(200,null,articleService.getArticlesByPageNumOrderByViewNum(pageNum));
    }

    /*
     * 发布文章
     * 从token中获取发布者信息、自动获取当前系统时间，与文章一同存入数据库
     */
    @PostMapping("/saveArticle")
    public ResponseResult<Boolean> saveArticle(@RequestBody Article article, HttpServletRequest request){
        String token = request.getHeader("token");
        User user = tokenUtils.decodeTokenToUser(token);

        article.setAuthorId(user.getUid());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        article.setPunishTime(formatter.format(System.currentTimeMillis()));
        article.setViewNum(0);
        return articleService.saveOrUpdateArticle(article);
    }


     /*
      * 修改文章 by id 由于mp提供的saveOrUpdate方法判断插入还是更新的依据是是否传入主键id
      * 修改时间
      */
    @PostMapping("/updateArticle")
    public ResponseResult<Boolean> updateArticle(@RequestBody Article article){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        article.setLastTimeUpdate(formatter.format(System.currentTimeMillis()));
        return articleService.saveOrUpdateArticle(article);
    }
    /*
     * 删除文章
     * 再整一个表暂存删除的文章，指定时间内可以找回
     */
    @DeleteMapping("/deleteArticle/{id}")
    public ResponseResult<Boolean> deleteArticleById(@PathVariable Integer id){
        return articleService.deleteArticle(id);
    }

    /*
     * 点击帖子，帖子的阅读量增加（会存在并发问题）
     * 可以考虑存在redis中，这样就不需要每次都去查一次数据库中的viewNum
     */
    @PostMapping("/viewNumInc/{articleId}")
    public ResponseResult<Boolean> viewNumInc(@PathVariable Integer articleId){
        return articleService.incViewNum(articleId);
    }

    /*
     * 2024/5/7 是否可以给content添加前缀索引，来提升检索的速度
     * 搜索一个帖子根据 title->summary->content的顺序来查找
     * 目前为纯字符串匹配，后续可以考虑更细的粒度，在字符串中分词，然后用分词来查找
     */
    @GetMapping("/queryByWords")
    public ResponseResult<List<Article>> queryArticlesByWords(@Param("words")String words){
        return articleService.queryArticleByWords(words);
    }

    @GetMapping("/category/VN")
    public ResponseResult<List<Article>> getByCateOrderByVN(@Param("cateName") String cateName){
        return articleService.getArticleByCateOrderByViewNum(cateName);
    }

    @GetMapping("/category/time")
    public ResponseResult<List<Article>> getByCateOrderByTime(@Param("cateName") String cateName){
        return articleService.getArticleByCateOrderByTime(cateName);
    }
}
