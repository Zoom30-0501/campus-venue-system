package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_suggestion")
public class TbSuggestion {
    @TableId(value = "suggestion_id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String title;

    private String content;

    @TableField(exist = false)
    private String type;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
