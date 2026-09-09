package com.club.venue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_venue_schedule")
public class TbVenueSchedule {
    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Long id;

    private Long venueId;
    private Long activityId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
