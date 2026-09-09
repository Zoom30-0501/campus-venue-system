package com.club.venue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.venue.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
    boolean register(SysUser user);
    SysUser getByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    List<SysUser> listUsers(String keyword);
}
