package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.TbFacility;
import com.club.venue.service.TbFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    @Autowired
    private TbFacilityService tbFacilityService;

    @GetMapping("/list")
    public Result<List<TbFacility>> list() {
        return Result.ok(tbFacilityService.list());
    }

    @GetMapping("/{id}")
    public Result<TbFacility> getById(@PathVariable Long id) {
        TbFacility facility = tbFacilityService.getById(id);
        if (facility == null) {
            return Result.error("设施不存在");
        }
        return Result.ok(facility);
    }

    @PostMapping
    public Result<?> save(@RequestBody TbFacility facility) {
        facility.setCreateTime(LocalDateTime.now());
        facility.setUpdateTime(LocalDateTime.now());
        tbFacilityService.save(facility);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody TbFacility facility) {
        if (facility.getFacilityId() == null) {
            return Result.error("设施ID不能为空");
        }
        TbFacility dbFacility = tbFacilityService.getById(facility.getFacilityId());
        if (dbFacility == null) {
            return Result.error("设施不存在");
        }
        facility.setUpdateTime(LocalDateTime.now());
        tbFacilityService.updateById(facility);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tbFacilityService.removeById(id);
        return Result.ok();
    }
}
