package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbClubMember;
import com.club.venue.service.TbClubMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/club-member")
public class ClubMemberController {

    @Autowired
    private TbClubMemberService tbClubMemberService;

    @GetMapping("/list/{clubId}")
    public Result<List<TbClubMember>> listByClub(@PathVariable Long clubId) {
        return Result.ok(tbClubMemberService.getMembersByClubId(clubId));
    }

    @PostMapping
    public Result<?> addMember(@RequestBody TbClubMember member) {
        member.setStatus("在任");
        tbClubMemberService.save(member);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> updateMember(@PathVariable Long id, @RequestBody TbClubMember member) {
        member.setId(id);
        tbClubMemberService.updateById(member);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> removeMember(@PathVariable Long id) {
        tbClubMemberService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/user/{userId}")
    public Result<List<TbClubMember>> listByUser(@PathVariable Long userId) {
        return Result.ok(tbClubMemberService.lambdaQuery()
                .eq(TbClubMember::getUserId, userId).list());
    }
}
