package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbActivity;
import com.club.venue.entity.TbClub;
import com.club.venue.service.TbActivityService;
import com.club.venue.service.TbClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Autowired
    private TbClubService tbClubService;

    @Autowired
    private TbActivityService tbActivityService;

    @GetMapping("/list")
    public Result<List<TbClub>> list() {
        return Result.ok(tbClubService.list());
    }

    @GetMapping("/{id}")
    public Result<TbClub> getById(@PathVariable Long id) {
        TbClub club = tbClubService.getById(id);
        if (club == null) {
            return Result.error("社团不存在");
        }
        return Result.ok(club);
    }

    @PostMapping
    public Result<?> save(@RequestBody TbClub club) {
        club.setCreateTime(LocalDateTime.now());
        club.setUpdateTime(LocalDateTime.now());
        tbClubService.save(club);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody TbClub club) {
        if (club.getClubId() == null) {
            return Result.error("社团ID不能为空");
        }
        TbClub dbClub = tbClubService.getById(club.getClubId());
        if (dbClub == null) {
            return Result.error("社团不存在");
        }
        club.setUpdateTime(LocalDateTime.now());
        tbClubService.updateById(club);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tbClubService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/{clubId}/activities")
    public Result<List<TbActivity>> getClubActivities(@PathVariable Long clubId) {
        return Result.ok(tbActivityService.lambdaQuery()
                .eq(TbActivity::getClubId, clubId)
                .orderByDesc(TbActivity::getCreateTime).list());
    }
}
