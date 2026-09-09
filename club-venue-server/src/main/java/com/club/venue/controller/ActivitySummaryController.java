package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbActivitySummary;
import com.club.venue.service.TbActivitySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activity-summary")
public class ActivitySummaryController {

    @Autowired
    private TbActivitySummaryService summaryService;

    @PostMapping
    public Result<?> save(@RequestBody TbActivitySummary summary) {
        summaryService.save(summary);
        return Result.ok();
    }

    @GetMapping("/byActivity/{activityId}")
    public Result<TbActivitySummary> getByActivity(@PathVariable Long activityId) {
        TbActivitySummary summary = summaryService.lambdaQuery()
                .eq(TbActivitySummary::getActivityId, activityId).one();
        return Result.ok(summary);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody TbActivitySummary summary) {
        summary.setId(id);
        summaryService.updateById(summary);
        return Result.ok();
    }
}
