package com.club.venue.controller;

import com.club.venue.common.JwtUtil;
import com.club.venue.common.Result;
import com.club.venue.entity.SysUser;
import com.club.venue.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        try {
            SysUser user = sysUserService.login(username, password);
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getUserId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("avatar", user.getAvatar());
            data.put("role", user.getRole());
            data.put("clubId", user.getClubId());
            data.put("clubName", user.getClubName());
            return Result.ok(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        try {
            sysUserService.register(user);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<SysUser> getInfo(@RequestParam String username) {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping
    public Result<?> update(@RequestBody SysUser user) {
        if (user.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        SysUser dbUser = sysUserService.getById(user.getUserId());
        if (dbUser == null) {
            return Result.error("用户不存在");
        }
        dbUser.setNickname(user.getNickname());
        if (user.getAvatar() != null) {
            dbUser.setAvatar(user.getAvatar());
        }
        if (user.getClubId() != null) {
            dbUser.setClubId(user.getClubId());
        }
        if (user.getClubName() != null) {
            dbUser.setClubName(user.getClubName());
        }
        // 角色修改需通过管理员专用接口，防止普通用户提权
        sysUserService.updateById(dbUser);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        try {
            sysUserService.changePassword(username, oldPassword, newPassword);
            return Result.ok();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<SysUser>> list(@RequestParam(required = false) String keyword) {
        List<SysUser> users = sysUserService.listUsers(keyword);
        users.forEach(u -> u.setPassword(null));
        return Result.ok(users);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }
}
