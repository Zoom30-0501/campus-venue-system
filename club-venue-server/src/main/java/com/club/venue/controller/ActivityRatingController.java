package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbActivityRating;
import com.club.venue.service.TbActivityRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-rating")
public class ActivityRatingController {

    @Autowired
    private TbActivityRatingService ratingService;

    @PostMapping
    public Result<?> rate(@RequestBody TbActivityRating rating) {
        ratingService.save(rating);
        return Result.ok();
    }

    @GetMapping("/list/{activityId}")
    public Result<List<TbActivityRating>> listByActivity(@PathVariable Long activityId) {
        return Result.ok(ratingService.lambdaQuery()
                .eq(TbActivityRating::getActivityId, activityId)
                .orderByDesc(TbActivityRating::getCreateTime).list());
    }

    @GetMapping("/my/{userId}")
    public Result<List<TbActivityRating>> listByUser(@PathVariable Long userId) {
        return Result.ok(ratingService.lambdaQuery()
                .eq(TbActivityRating::getUserId, userId).list());
    }
}
