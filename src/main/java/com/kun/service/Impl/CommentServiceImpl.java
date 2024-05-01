package com.kun.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kun.controller.enums.StateCodeEnum;
import com.kun.dao.CommentMapper;
import com.kun.dto.CommentDTO;
import com.kun.entity.Comment;
import com.kun.entity.ResponseResult;
import com.kun.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    private LambdaQueryWrapper<Comment> wrapper;
    public CommentServiceImpl(){
        wrapper = new LambdaQueryWrapper<>();
    }
    /*
     * 根据当前文章的id，查询所有的评论，以及每条评论的子评论
     */
    @Override
    public ResponseResult<List<CommentDTO>> getCommentsByArticleId(Integer articleId) {
        try {
            wrapper.eq(Comment::getArticleId,articleId);
            List<Comment> list = commentMapper.selectList(wrapper);
            wrapper.clear();
            Map<Integer,List<Comment>> map = new HashMap<>();
            list.forEach(article->{
                Integer fid = article.getFatherId();
                if(fid!=null){
//                    map.computeIfAbsent(fid,k->List.of(article));
                    if(!map.containsKey(fid)) map.put(fid,List.of(article));
                    else{
                        List<Comment> commentList = new ArrayList<>(map.get(fid));
                        commentList.add(article);
                        map.put(fid,commentList);
                    }
                }
            });
            // 对于fatherId为null的评论，它为父评论，如果map中存在它的子评论，添加到字段中
            List<CommentDTO> resList = new ArrayList<>(list.stream().map(comment -> {
                if (comment.getFatherId() == null) {
                    CommentDTO commentDTO = new CommentDTO();
                    commentDTO.copyFrom(comment);
                    if (map.containsKey(comment.getId()))
                        commentDTO.setSonComments(map.get(comment.getId()));
                    return commentDTO;
                }
                return null;
            }).toList());
            resList.removeIf(Objects::isNull);
            return new ResponseResult<>(StateCodeEnum.GET_COMMENTS_SUCCESS.getCode(), StateCodeEnum.GET_COMMENTS_SUCCESS.getMsg(), resList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseResult<>(StateCodeEnum.GET_COMMENTS_FAIL.getCode(), StateCodeEnum.GET_COMMENTS_FAIL.getMsg(), null);
    }

    @Override
    public ResponseResult<Boolean> publishComment(Comment comment) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String publishTime = formatter.format(System.currentTimeMillis());
        comment.setPublishTime(publishTime);

        boolean effectRows = this.save(comment);
        if(effectRows)
            return new ResponseResult<>(StateCodeEnum.PUBLISH_COMMENT_SUCCESS.getCode(), StateCodeEnum.PUBLISH_COMMENT_SUCCESS.getMsg(), effectRows);
        return new ResponseResult<>(StateCodeEnum.PUBLISH_COMMENT_FAIL.getCode(), StateCodeEnum.PUBLISH_COMMENT_FAIL.getMsg(), effectRows);
    }
}
