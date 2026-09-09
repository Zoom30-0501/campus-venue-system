package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_club_application")
public class TbClubApplication {
    @TableId(value = "application_id", type = IdType.AUTO)
    private Long id;

    private Long clubId;

    @TableField(exist = false)
    private String clubName;

    private Long userId;

    @TableField(exist = false)
    private String userName;

    private String reason;
    private String status;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
