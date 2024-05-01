package com.kun.controller;

import com.kun.dto.CommentDTO;
import com.kun.entity.Comment;
import com.kun.entity.ResponseResult;
import com.kun.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @GetMapping("/get/{articleId}")
    public ResponseResult<List<CommentDTO>> getCommentsByArticleId(@PathVariable Integer articleId){
        return commentService.getCommentsByArticleId(articleId);
    }

    @PostMapping("/publish")
    public ResponseResult<Boolean> publishComment(@RequestBody Comment comment){
        return commentService.publishComment(comment);
    }
}
