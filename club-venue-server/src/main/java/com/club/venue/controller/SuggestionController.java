package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbSuggestion;
import com.club.venue.service.TbSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/suggestion")
public class SuggestionController {

    @Autowired
    private TbSuggestionService tbSuggestionService;

    @GetMapping("/list")
    public Result<List<TbSuggestion>> list() {
        return Result.ok(tbSuggestionService.list());
    }

    @PostMapping
    public Result<?> submit(@RequestBody TbSuggestion suggestion) {
        suggestion.setStatus("待处理");
        suggestion.setCreateTime(LocalDateTime.now());
        tbSuggestionService.save(suggestion);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<?> handle(@PathVariable Long id, @RequestParam String status) {
        TbSuggestion s = tbSuggestionService.getById(id);
        if (s == null) return Result.error("建议不存在");
        s.setStatus(status);
        tbSuggestionService.updateById(s);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tbSuggestionService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/user/{userId}")
    public Result<List<TbSuggestion>> listByUser(@PathVariable Long userId) {
        return Result.ok(tbSuggestionService.lambdaQuery()
                .eq(TbSuggestion::getUserId, userId)
                .orderByDesc(TbSuggestion::getCreateTime).list());
    }
}
