package com.kun.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("article")
public class Article {
    @TableId
    private Integer id;
    private String title;
    private String summary;

    private Integer authorId;
    private String punishTime;
    private String lastTimeUpdate;
    private Integer viewNum;
    private String content;

    private Integer category;

}
