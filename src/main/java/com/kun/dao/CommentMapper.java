package com.kun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kun.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
