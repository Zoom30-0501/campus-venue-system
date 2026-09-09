package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_check_in")
public class TbCheckIn {
    @TableId(value = "check_in_id", type = IdType.AUTO)
    private Long id;

    private Long activityId;
    private Long userId;

    @TableField(exist = false)
    private String userName;

    private LocalDateTime checkInTime;

    @TableField(exist = false)
    private String location;

    private Integer verified;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
