package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbActivity;
import com.club.venue.entity.TbVenueSchedule;
import com.club.venue.service.ProcessService;
import com.club.venue.service.TbActivityService;
import com.club.venue.service.TbVenueScheduleService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private TbActivityService tbActivityService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private TbVenueScheduleService tbVenueScheduleService;

    @GetMapping("/list")
    public Result<List<TbActivity>> list(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long clubId) {
        if (userId != null) {
            return Result.ok(tbActivityService.lambdaQuery()
                    .eq(TbActivity::getApplicantId, userId)
                    .orderByDesc(TbActivity::getCreateTime).list());
        }
        if (clubId != null) {
            return Result.ok(tbActivityService.lambdaQuery()
                    .eq(TbActivity::getClubId, clubId)
                    .orderByDesc(TbActivity::getCreateTime).list());
        }
        return Result.ok(tbActivityService.list());
    }

    @GetMapping("/{id}")
    public Result<TbActivity> getById(@PathVariable Long id) {
        TbActivity activity = tbActivityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.ok(activity);
    }

    @PostMapping
    public Result<?> save(@RequestBody TbActivity activity) {
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activity.setStatus("待审核");
        tbActivityService.save(activity);
        try {
            processService.startProcess(
                String.valueOf(activity.getActivityId()),
                activity.getActivityName(),
                activity.getActivityName()
            );
        } catch (Exception e) {
            // 流程启动失败不影响活动保存
        }
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody TbActivity activity) {
        if (activity.getActivityId() == null) {
            return Result.error("活动ID不能为空");
        }
        TbActivity dbActivity = tbActivityService.getById(activity.getActivityId());
        if (dbActivity == null) {
            return Result.error("活动不存在");
        }
        activity.setUpdateTime(LocalDateTime.now());
        tbActivityService.updateById(activity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tbActivityService.removeById(id);
        return Result.ok();
    }

    @PreAuthorize("hasAnyRole('ADMIN','CLUB_LEADER')")
    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String comment) {
        TbActivity activity = tbActivityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }

        if ("已通过".equals(status)) {
            // 场地时段冲突检测
            if (activity.getVenueId() != null && activity.getStartTime() != null && activity.getEndTime() != null) {
                long conflictCount = tbActivityService.lambdaQuery()
                    .eq(TbActivity::getVenueId, activity.getVenueId())
                    .in(TbActivity::getStatus, "已通过", "进行中")
                    .ne(TbActivity::getActivityId, activity.getActivityId())
                    .lt(TbActivity::getStartTime, activity.getEndTime())
                    .gt(TbActivity::getEndTime, activity.getStartTime())
                    .count();
                if (conflictCount > 0) {
                    return Result.error("场地时段冲突：该场地在选定时间段已被其他活动占用");
                }
            }

            // 判断是否需要二级审核（活动时长 > 48小时）
            long durationHours = java.time.Duration.between(activity.getStartTime(), activity.getEndTime()).toHours();
            if (durationHours > 48) {
                activity.setStatus("一级审核通过");
                activity.setUpdateTime(LocalDateTime.now());
                tbActivityService.updateById(activity);
                return Result.ok("活动时长超过48小时，已通过一级审核，请等待二级审核");
            }
        }

        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        tbActivityService.updateById(activity);

        // 审核通过后自动锁定场地时段
        if ("已通过".equals(status) && activity.getVenueId() != null) {
            TbVenueSchedule schedule = new TbVenueSchedule();
            schedule.setVenueId(activity.getVenueId());
            schedule.setActivityId(activity.getActivityId());
            schedule.setTitle(activity.getActivityName());
            schedule.setStartTime(activity.getStartTime());
            schedule.setEndTime(activity.getEndTime());
            schedule.setStatus("已锁定");
            tbVenueScheduleService.save(schedule);
        }

        try {
            Task task = processService.getTaskByBusinessKey(String.valueOf(id));
            if (task != null) {
                Map<String, Object> extraVars = new HashMap<>();
                if (activity.getStartTime() != null && activity.getEndTime() != null) {
                    long hours = java.time.Duration.between(activity.getStartTime(), activity.getEndTime()).toHours();
                    extraVars.put("durationHours", hours);
                }
                processService.approveTask(task.getId(), "已通过".equals(status), comment, extraVars);
            }
        } catch (Exception ignored) {
        }
        return Result.ok();
    }

    @PutMapping("/second-approve/{id}")
    public Result<?> secondApprove(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) String comment) {
        TbActivity activity = tbActivityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (!"一级审核通过".equals(activity.getStatus())) {
            return Result.error("当前活动状态不是一级审核通过，无法进行二级审核");
        }
        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        tbActivityService.updateById(activity);

        // 二级审核通过后自动锁定场地时段
        if ("已通过".equals(status) && activity.getVenueId() != null) {
            TbVenueSchedule schedule = new TbVenueSchedule();
            schedule.setVenueId(activity.getVenueId());
            schedule.setActivityId(activity.getActivityId());
            schedule.setTitle(activity.getActivityName());
            schedule.setStartTime(activity.getStartTime());
            schedule.setEndTime(activity.getEndTime());
            schedule.setStatus("已锁定");
            tbVenueScheduleService.save(schedule);
        }

        try {
            Task task = processService.getTaskByBusinessKey(String.valueOf(id));
            if (task != null) {
                processService.approveTask(task.getId(), "已通过".equals(status), comment);
            }
        } catch (Exception ignored) {
        }
        return Result.ok();
    }

    @GetMapping("/pending-tasks")
    public Result<List<Map<String, Object>>> pendingTasks(@RequestParam(required = false) String username) {
        return Result.ok(processService.getPendingTasks(username));
    }
}
