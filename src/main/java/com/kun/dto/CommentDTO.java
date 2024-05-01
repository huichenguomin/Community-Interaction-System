package com.kun.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.kun.entity.Comment;
import lombok.Data;

import java.util.List;

@Data
public class CommentDTO {
    @TableId
    private Integer id;
    private String content;
    private Integer uid;
    private Integer articleId;
    private Integer fatherId;
    private String publishTime;
    private List<Comment> sonComments;

    /*
     * 是否可以创建一个工具类来实现安全地对象间相同属性的内容克隆
     */
    public void copyFrom(Comment comment){
        this.id = comment.getId();
        this.articleId = comment.getArticleId();
        this.content = comment.getContent();
        this.uid = comment.getUid();
        this.fatherId = comment.getFatherId();
        this.publishTime = comment.getPublishTime();
    }


}
