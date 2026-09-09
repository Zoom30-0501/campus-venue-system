package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbVenueSchedule;
import com.club.venue.mapper.TbVenueScheduleMapper;
import com.club.venue.service.TbVenueScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TbVenueScheduleServiceImpl extends ServiceImpl<TbVenueScheduleMapper, TbVenueSchedule> implements TbVenueScheduleService {

    @Override
    public List<TbVenueSchedule> getSchedulesByDateRange(Long venueId, LocalDateTime start, LocalDateTime end) {
        return lambdaQuery()
            .eq(TbVenueSchedule::getVenueId, venueId)
            .lt(TbVenueSchedule::getStartTime, end)
            .gt(TbVenueSchedule::getEndTime, start)
            .list();
    }
}
