package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.SysUser;
import com.club.venue.entity.TbClub;
import com.club.venue.entity.TbClubApplication;
import com.club.venue.entity.TbClubMember;
import com.club.venue.mapper.SysUserMapper;
import com.club.venue.mapper.TbClubMapper;
import com.club.venue.mapper.TbClubMemberMapper;
import com.club.venue.service.TbClubApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/club-application")
public class ClubApplicationController {

    @Autowired
    private TbClubApplicationService applicationService;

    @Autowired
    private TbClubMapper clubMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private TbClubMemberMapper memberMapper;

    @PostMapping
    public Result<?> apply(@RequestBody TbClubApplication application) {
        // 检查是否已是该社团成员
        Long memberCount = memberMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TbClubMember>()
                        .eq(TbClubMember::getClubId, application.getClubId())
                        .eq(TbClubMember::getUserId, application.getUserId())
                        .eq(TbClubMember::getDeleted, 0));
        if (memberCount > 0) {
            return Result.error("你已经是该社团的成员，无需重复申请");
        }
        // 检查是否已有待审核的申请
        Long pendingCount = applicationService.lambdaQuery()
                .eq(TbClubApplication::getClubId, application.getClubId())
                .eq(TbClubApplication::getUserId, application.getUserId())
                .eq(TbClubApplication::getStatus, "待审核")
                .eq(TbClubApplication::getDeleted, 0).count();
        if (pendingCount > 0) {
            return Result.error("你已提交过申请，请等待审核结果");
        }
        application.setStatus("待审核");
        applicationService.save(application);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<List<TbClubApplication>> listAll() {
        List<TbClubApplication> list = applicationService.lambdaQuery()
                .eq(TbClubApplication::getDeleted, 0).list();
        fillNames(list);
        return Result.ok(list);
    }

    @GetMapping("/list/{clubId}")
    public Result<List<TbClubApplication>> listByClub(@PathVariable Long clubId) {
        List<TbClubApplication> list = applicationService.lambdaQuery()
                .eq(TbClubApplication::getClubId, clubId)
                .eq(TbClubApplication::getDeleted, 0).list();
        fillNames(list);
        return Result.ok(list);
    }

    @GetMapping("/my/{userId}")
    public Result<List<TbClubApplication>> listByUser(@PathVariable Long userId) {
        List<TbClubApplication> list = applicationService.lambdaQuery()
                .eq(TbClubApplication::getUserId, userId)
                .eq(TbClubApplication::getDeleted, 0).list();
        fillNames(list);
        return Result.ok(list);
    }

    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam String status) {
        TbClubApplication app = applicationService.getById(id);
        app.setStatus(status);
        applicationService.updateById(app);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        applicationService.removeById(id);
        return Result.ok();
    }

    private void fillNames(List<TbClubApplication> list) {
        for (TbClubApplication app : list) {
            if (app.getClubId() != null) {
                TbClub club = clubMapper.selectById(app.getClubId());
                if (club != null) app.setClubName(club.getClubName());
            }
            if (app.getUserId() != null) {
                SysUser user = userMapper.selectById(app.getUserId());
                if (user != null) app.setUserName(user.getNickname());
            }
        }
    }
}
