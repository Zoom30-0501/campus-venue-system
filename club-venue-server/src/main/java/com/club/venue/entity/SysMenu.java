package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long menuId;

    private Long parentId;
    private String menuName;
    private String menuType;
    private String permission;
    private String path;
    private String icon;
    private Integer sort;
    private String status;

    private Integer deleted;

    @TableField(exist = false)
    private List<SysMenu> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
