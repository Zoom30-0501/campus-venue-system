package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbCheckIn;
import com.club.venue.service.TbCheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private TbCheckInService tbCheckInService;

    @GetMapping("/activity/{activityId}")
    public Result<List<TbCheckIn>> listByActivity(@PathVariable Long activityId) {
        return Result.ok(tbCheckInService.getCheckInsByActivity(activityId));
    }

    @GetMapping("/count/{activityId}")
    public Result<Long> count(@PathVariable Long activityId) {
        return Result.ok(tbCheckInService.getCheckInCount(activityId));
    }

    @PostMapping
    public Result<?> checkIn(@RequestBody TbCheckIn checkIn) {
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setVerified(0);
        tbCheckInService.save(checkIn);
        return Result.ok();
    }

    @PutMapping("/verify/{id}")
    public Result<?> verify(@PathVariable Long id) {
        TbCheckIn c = tbCheckInService.getById(id);
        if (c == null) return Result.error("签到记录不存在");
        c.setVerified(1);
        tbCheckInService.updateById(c);
        return Result.ok();
    }

    @GetMapping("/user/{userId}")
    public Result<List<TbCheckIn>> listByUser(@PathVariable Long userId) {
        return Result.ok(tbCheckInService.lambdaQuery()
                .eq(TbCheckIn::getUserId, userId)
                .orderByDesc(TbCheckIn::getCreateTime).list());
    }
}
