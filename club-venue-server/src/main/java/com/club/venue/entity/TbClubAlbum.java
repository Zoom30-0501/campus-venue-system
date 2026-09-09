package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_club_album")
public class TbClubAlbum {
    @TableId(value = "album_id", type = IdType.AUTO)
    private Long id;

    private Long clubId;

    @TableField("url")
    private String imageUrl;

    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
