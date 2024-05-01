package com.kun.service;

import com.kun.dto.CommentDTO;
import com.kun.entity.Comment;
import com.kun.entity.ResponseResult;

import java.util.List;

public interface CommentService {
    ResponseResult<List<CommentDTO>> getCommentsByArticleId(Integer articleId);
    ResponseResult<Boolean> publishComment(Comment comment);
}
