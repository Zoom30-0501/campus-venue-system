package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbCheckIn;
import com.club.venue.mapper.TbCheckInMapper;
import com.club.venue.service.TbCheckInService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbCheckInServiceImpl extends ServiceImpl<TbCheckInMapper, TbCheckIn> implements TbCheckInService {

    @Override
    public List<TbCheckIn> getCheckInsByActivity(Long activityId) {
        return lambdaQuery().eq(TbCheckIn::getActivityId, activityId).list();
    }

    @Override
    public long getCheckInCount(Long activityId) {
        return lambdaQuery().eq(TbCheckIn::getActivityId, activityId).count();
    }
}
