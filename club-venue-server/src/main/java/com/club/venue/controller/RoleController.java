package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.SysRole;
import com.club.venue.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.ok(sysRoleService.list());
    }

    @PostMapping
    public Result<?> save(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }
}
