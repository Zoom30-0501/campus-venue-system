package com.club.venue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.venue.entity.TbVenueSchedule;

import java.time.LocalDateTime;
import java.util.List;

public interface TbVenueScheduleService extends IService<TbVenueSchedule> {
    List<TbVenueSchedule> getSchedulesByDateRange(Long venueId, LocalDateTime start, LocalDateTime end);
}
