package com.club.venue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.venue.entity.TbCheckIn;

import java.util.List;

public interface TbCheckInService extends IService<TbCheckIn> {
    List<TbCheckIn> getCheckInsByActivity(Long activityId);
    long getCheckInCount(Long activityId);
}
