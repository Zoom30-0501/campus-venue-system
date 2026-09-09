package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_activity")
public class TbActivity {
    @TableId(type = IdType.AUTO)
    private Long activityId;

    private String activityName;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long applicantId;
    private Long clubId;
    private Long venueId;
    private String poster;
    private String status;

    private Long approverId;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
