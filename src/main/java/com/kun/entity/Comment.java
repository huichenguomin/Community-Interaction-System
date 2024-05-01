package com.kun.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("comment")
public class Comment {
    @TableId
    private Integer id;
    private String content;
    private Integer uid;
    private Integer articleId;
    private Integer fatherId;
    private String publishTime;
}
