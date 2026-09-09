package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_activity_summary")
public class TbActivitySummary {
    @TableId(value = "summary_id", type = IdType.AUTO)
    private Long id;

    private Long activityId;

    @TableField("summary_content")
    private String content;

    private String images;
    private Integer participantCount;
    private String feedback;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
