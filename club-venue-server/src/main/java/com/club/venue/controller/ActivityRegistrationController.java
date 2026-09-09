package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbActivityRegistration;
import com.club.venue.service.TbActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-registration")
public class ActivityRegistrationController {

    @Autowired
    private TbActivityRegistrationService registrationService;

    /** 用户报名参加活动 */
    @PostMapping
    public Result<?> register(@RequestBody TbActivityRegistration registration) {
        // 检查是否已报名
        long count = registrationService.lambdaQuery()
                .eq(TbActivityRegistration::getActivityId, registration.getActivityId())
                .eq(TbActivityRegistration::getUserId, registration.getUserId())
                .count();
        if (count > 0) {
            return Result.error("您已报名该活动，请勿重复报名");
        }
        registration.setStatus("待审核");
        registrationService.save(registration);
        return Result.ok("报名成功，等待审核");
    }

    /** 查看某个活动的所有报名 */
    @GetMapping("/list/{activityId}")
    public Result<List<TbActivityRegistration>> listByActivity(@PathVariable Long activityId) {
        return Result.ok(registrationService.lambdaQuery()
                .eq(TbActivityRegistration::getActivityId, activityId)
                .orderByDesc(TbActivityRegistration::getCreateTime).list());
    }

    /** 查看某用户的报名记录 */
    @GetMapping("/my/{userId}")
    public Result<List<TbActivityRegistration>> listByUser(@PathVariable Long userId) {
        return Result.ok(registrationService.lambdaQuery()
                .eq(TbActivityRegistration::getUserId, userId)
                .orderByDesc(TbActivityRegistration::getCreateTime).list());
    }

    /** 审核报名（通过/驳回） */
    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam String status) {
        TbActivityRegistration reg = registrationService.getById(id);
        if (reg == null) return Result.error("报名记录不存在");
        reg.setStatus(status);
        registrationService.updateById(reg);
        return Result.ok();
    }

    /** 取消报名 */
    @DeleteMapping("/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        registrationService.removeById(id);
        return Result.ok();
    }
}
