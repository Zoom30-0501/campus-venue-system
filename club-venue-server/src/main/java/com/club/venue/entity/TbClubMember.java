package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_club_member")
public class TbClubMember {
    @TableId(value = "member_id", type = IdType.AUTO)
    private Long id;

    private Long clubId;
    private Long userId;
    private String role;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String status;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
