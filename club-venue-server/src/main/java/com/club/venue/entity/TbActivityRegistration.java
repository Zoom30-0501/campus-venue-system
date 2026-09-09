package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_activity_registration")
public class TbActivityRegistration {
    @TableId(value = "registration_id", type = IdType.AUTO)
    private Long id;

    private Long activityId;
    private Long userId;
    private String status;  // 待审核 / 已通过 / 已驳回

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
