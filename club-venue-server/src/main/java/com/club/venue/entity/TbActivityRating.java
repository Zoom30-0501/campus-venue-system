package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_activity_rating")
public class TbActivityRating {
    @TableId(value = "rating_id", type = IdType.AUTO)
    private Long id;

    private Long activityId;
    private Long userId;

    @TableField(exist = false)
    private String userName;

    private Integer score;
    private String comment;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
