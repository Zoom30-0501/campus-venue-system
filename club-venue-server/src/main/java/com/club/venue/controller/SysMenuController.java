package com.club.venue.controller;

import com.club.venue.common.Result;
import com.club.venue.entity.SysMenu;
import com.club.venue.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
@PreAuthorize("hasRole('ADMIN')")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        return Result.ok(sysMenuService.list());
    }

    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> allMenus = sysMenuService.list();
        List<SysMenu> roots = allMenus.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .collect(Collectors.toList());
        for (SysMenu root : roots) {
            buildChildren(root, allMenus);
        }
        return Result.ok(roots);
    }

    private void buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(m -> m.getParentId() != null && m.getParentId().equals(parent.getMenuId()))
                .collect(Collectors.toList());
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (SysMenu child : children) {
                buildChildren(child, allMenus);
            }
        }
    }

    @PostMapping
    public Result<?> save(@RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return Result.ok();
    }

    @PutMapping
    public Result<?> update(@RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysMenuService.removeById(id);
        return Result.ok();
    }
}
