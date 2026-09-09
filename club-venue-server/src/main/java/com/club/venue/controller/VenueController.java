package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbVenue;
import com.club.venue.entity.TbVenueSchedule;
import com.club.venue.service.TbVenueScheduleService;
import com.club.venue.service.TbVenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/venue")
public class VenueController {

    @Autowired
    private TbVenueService tbVenueService;

    @Autowired
    private TbVenueScheduleService tbVenueScheduleService;

    @GetMapping("/list")
    public Result<List<TbVenue>> list() {
        return Result.ok(tbVenueService.list());
    }

    @GetMapping("/{id}")
    public Result<TbVenue> getById(@PathVariable Long id) {
        TbVenue venue = tbVenueService.getById(id);
        if (venue == null) {
            return Result.error("场地不存在");
        }
        return Result.ok(venue);
    }

    @PostMapping
    public Result<?> save(@RequestBody TbVenue venue) {
        venue.setCreateTime(LocalDateTime.now());
        venue.setUpdateTime(LocalDateTime.now());
        tbVenueService.save(venue);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody TbVenue venue) {
        if (venue.getVenueId() == null) {
            return Result.error("场地ID不能为空");
        }
        TbVenue dbVenue = tbVenueService.getById(venue.getVenueId());
        if (dbVenue == null) {
            return Result.error("场地不存在");
        }
        venue.setUpdateTime(LocalDateTime.now());
        tbVenueService.updateById(venue);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tbVenueService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/{id}/schedule")
    public Result<List<TbVenueSchedule>> getSchedule(
            @PathVariable Long id,
            @RequestParam(required = false) String date) {
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();
        return Result.ok(tbVenueScheduleService.getSchedulesByDateRange(id, start, end));
    }

    @GetMapping("/{id}/week-schedule")
    public Result<List<TbVenueSchedule>> getWeekSchedule(@PathVariable Long id) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(7).atStartOfDay();
        return Result.ok(tbVenueScheduleService.getSchedulesByDateRange(id, start, end));
    }
}
