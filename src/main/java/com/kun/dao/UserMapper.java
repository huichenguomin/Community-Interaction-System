package com.kun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kun.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
