package com.club.venue.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实体类测试 - 验证Lombok注解、字段映射、Getter/Setter
 */
@DisplayName("实体类测试")
class EntityTest {

    // ==================== SysUser ====================

    @Test
    @DisplayName("SysUser - 基本字段设置和获取")
    void testSysUser() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setNickname("张三");
        user.setUsername("zhangsan");
        user.setPassword("encoded_pass");
        user.setAvatar("/avatars/1.jpg");
        user.setClubId(1L);
        user.setClubName("计算机协会");
        user.setRole("club_leader");
        user.setDeleted(0);

        assertEquals(1L, user.getUserId());
        assertEquals("张三", user.getNickname());
        assertEquals("zhangsan", user.getUsername());
        assertEquals("encoded_pass", user.getPassword());
        assertEquals("/avatars/1.jpg", user.getAvatar());
        assertEquals(1L, user.getClubId());
        assertEquals("计算机协会", user.getClubName());
        assertEquals("club_leader", user.getRole());
        assertEquals(0, user.getDeleted());
    }

    @Test
    @DisplayName("SysUser - createTime/updateTime")
    void testSysUserTimestamps() {
        SysUser user = new SysUser();
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        assertEquals(now, user.getCreateTime());
        assertEquals(now, user.getUpdateTime());
    }

    // ==================== TbClub ====================

    @Test
    @DisplayName("TbClub - 基本字段")
    void testTbClub() {
        TbClub club = new TbClub();
        club.setClubId(1L);
        club.setClubName("计算机协会");
        club.setAvatar("/avatars/club1.jpg");
        club.setLeaderId(2L);
        club.setLeaderName("张三");
        club.setCategory("学术科技");
        club.setPhone("13800001111");
        club.setIntroId(null);
        club.setDeleted(0);

        assertEquals(1L, club.getClubId());
        assertEquals("计算机协会", club.getClubName());
        assertEquals("学术科技", club.getCategory());
        assertEquals("13800001111", club.getPhone());
        assertEquals(2L, club.getLeaderId());
        assertEquals("张三", club.getLeaderName());
    }

    // ==================== TbClubMember ====================

    @Test
    @DisplayName("TbClubMember - 基本字段和虚拟字段")
    void testTbClubMember() {
        TbClubMember member = new TbClubMember();
        member.setId(1L);
        member.setClubId(1L);
        member.setUserId(6L);
        member.setRole("成员");
        member.setDeleted(0);
        // 虚拟字段（@TableField(exist = false)）
        member.setUserName("testuser");
        member.setNickname("测试用户");
        member.setStatus("在任");

        assertEquals(1L, member.getId());
        assertEquals(1L, member.getClubId());
        assertEquals(6L, member.getUserId());
        assertEquals("成员", member.getRole());
        assertEquals("testuser", member.getUserName());
        assertEquals("测试用户", member.getNickname());
        assertEquals("在任", member.getStatus());
    }

    // ==================== TbClubApplication ====================

    @Test
    @DisplayName("TbClubApplication - 申请状态覆盖")
    void testTbClubApplication() {
        TbClubApplication app = new TbClubApplication();
        app.setId(1L);
        app.setClubId(1L);
        app.setUserId(7L);
        app.setReason("想加入社团");
        app.setStatus("待审核");
        app.setDeleted(0);

        assertEquals(1L, app.getId());
        assertEquals("待审核", app.getStatus());
        assertEquals("想加入社团", app.getReason());

        // 测试不同状态
        app.setStatus("已通过");
        assertEquals("已通过", app.getStatus());

        app.setStatus("已驳回");
        assertEquals("已驳回", app.getStatus());
    }

    // ==================== TbClubAlbum ====================

    @Test
    @DisplayName("TbClubAlbum - 基本字段")
    void testTbClubAlbum() {
        TbClubAlbum album = new TbClubAlbum();
        album.setId(1L);
        album.setClubId(1L);
        album.setImageUrl("https://example.com/photo.jpg");
        album.setDeleted(0);

        assertEquals(1L, album.getId());
        assertEquals("https://example.com/photo.jpg", album.getImageUrl());
    }

    // ==================== TbVenue ====================

    @Test
    @DisplayName("TbVenue - 场地状态覆盖")
    void testTbVenue() {
        TbVenue venue = new TbVenue();
        venue.setVenueId(1L);
        venue.setVenueName("多媒体教室A201");
        venue.setVenueNo("R001");
        venue.setStatus("可用");
        venue.setDeleted(0);

        assertEquals(1L, venue.getVenueId());
        assertEquals("多媒体教室A201", venue.getVenueName());
        assertEquals("R001", venue.getVenueNo());
        assertEquals("可用", venue.getStatus());

        // 测试所有状态
        venue.setStatus("使用中");
        assertEquals("使用中", venue.getStatus());

        venue.setStatus("维护中");
        assertEquals("维护中", venue.getStatus());
    }

    // ==================== TbFacility ====================

    @Test
    @DisplayName("TbFacility - 设施状态覆盖")
    void testTbFacility() {
        TbFacility facility = new TbFacility();
        facility.setFacilityId(1L);
        facility.setFacilityName("投影仪");
        facility.setStatus("正常");
        facility.setVenueId(1L);
        facility.setDeleted(0);

        assertEquals(1L, facility.getFacilityId());
        assertEquals("投影仪", facility.getFacilityName());
        assertEquals("正常", facility.getStatus());

        facility.setStatus("损坏");
        assertEquals("损坏", facility.getStatus());

        facility.setStatus("维修中");
        assertEquals("维修中", facility.getStatus());
    }

    // ==================== TbVenueSchedule ====================

    @Test
    @DisplayName("TbVenueSchedule - 排期字段")
    void testTbVenueSchedule() {
        TbVenueSchedule schedule = new TbVenueSchedule();
        schedule.setId(1L);
        schedule.setVenueId(1L);
        schedule.setActivityId(1L);
        schedule.setTitle("编程大赛");
        LocalDateTime start = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 10, 17, 0);
        schedule.setStartTime(start);
        schedule.setEndTime(end);
        schedule.setStatus("已锁定");

        assertEquals(1L, schedule.getId());
        assertEquals("编程大赛", schedule.getTitle());
        assertEquals(start, schedule.getStartTime());
        assertEquals(end, schedule.getEndTime());
        assertEquals("已锁定", schedule.getStatus());

        schedule.setStatus("已取消");
        assertEquals("已取消", schedule.getStatus());
    }

    // ==================== TbActivity ====================

    @Test
    @DisplayName("TbActivity - 完整活动字段")
    void testTbActivity() {
        TbActivity activity = new TbActivity();
        activity.setActivityId(1L);
        activity.setActivityName("编程大赛");
        activity.setReason("提升编程能力");
        LocalDateTime start = LocalDateTime.of(2026, 6, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 10, 17, 0);
        activity.setStartTime(start);
        activity.setEndTime(end);
        activity.setApplicantId(2L);
        activity.setClubId(1L);
        activity.setVenueId(1L);
        activity.setPoster("/posters/1.jpg");
        activity.setStatus("待审核");
        activity.setApproverId(null);
        activity.setDeleted(0);

        assertEquals(1L, activity.getActivityId());
        assertEquals("编程大赛", activity.getActivityName());
        assertEquals("提升编程能力", activity.getReason());
        assertEquals(start, activity.getStartTime());
        assertEquals(end, activity.getEndTime());
        assertEquals(2L, activity.getApplicantId());
        assertEquals(1L, activity.getClubId());
        assertEquals(1L, activity.getVenueId());
        assertEquals("待审核", activity.getStatus());
        assertNull(activity.getApproverId());
    }

    @Test
    @DisplayName("TbActivity - 活动状态流转")
    void testTbActivityStatusFlow() {
        TbActivity activity = new TbActivity();

        activity.setStatus("待审核");
        assertEquals("待审核", activity.getStatus());

        activity.setStatus("已通过");
        assertEquals("已通过", activity.getStatus());

        activity.setStatus("已驳回");
        assertEquals("已驳回", activity.getStatus());

        activity.setStatus("一级审核通过");
        assertEquals("一级审核通过", activity.getStatus());

        activity.setStatus("进行中");
        assertEquals("进行中", activity.getStatus());

        activity.setStatus("已结束");
        assertEquals("已结束", activity.getStatus());
    }

    // ==================== TbActivityRating ====================

    @Test
    @DisplayName("TbActivityRating - 评分1-5")
    void testTbActivityRating() {
        TbActivityRating rating = new TbActivityRating();
        rating.setId(1L);
        rating.setActivityId(1L);
        rating.setUserId(6L);
        rating.setScore(5);
        rating.setComment("很棒的活动！");
        rating.setDeleted(0);

        assertEquals(1L, rating.getId());
        assertEquals(5, rating.getScore());
        assertEquals("很棒的活动！", rating.getComment());

        // 边界值测试
        rating.setScore(1);
        assertEquals(1, rating.getScore());

        rating.setScore(3);
        assertEquals(3, rating.getScore());
    }

    // ==================== TbActivitySummary ====================

    @Test
    @DisplayName("TbActivitySummary - 活动总结字段")
    void testTbActivitySummary() {
        TbActivitySummary summary = new TbActivitySummary();
        summary.setId(1L);
        summary.setActivityId(1L);
        summary.setContent("活动总结内容...");
        summary.setImages("/images/summary1.jpg,/images/summary2.jpg");
        summary.setParticipantCount(85);
        summary.setFeedback("反馈内容");
        summary.setDeleted(0);

        assertEquals(1L, summary.getId());
        assertEquals("活动总结内容...", summary.getContent());
        assertEquals(85, summary.getParticipantCount());
        assertEquals("反馈内容", summary.getFeedback());
    }

    // ==================== TbCheckIn ====================

    @Test
    @DisplayName("TbCheckIn - 签到字段和虚拟字段")
    void testTbCheckIn() {
        TbCheckIn checkIn = new TbCheckIn();
        checkIn.setId(1L);
        checkIn.setActivityId(1L);
        checkIn.setUserId(6L);
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setVerified(0);
        // 虚拟字段
        checkIn.setUserName("testuser");
        checkIn.setLocation("东门签到点");

        assertEquals(1L, checkIn.getId());
        assertEquals(0, checkIn.getVerified());
        checkIn.setVerified(1);
        assertEquals(1, checkIn.getVerified());
        assertEquals("testuser", checkIn.getUserName());
        assertEquals("东门签到点", checkIn.getLocation());
    }

    // ==================== TbSuggestion ====================

    @Test
    @DisplayName("TbSuggestion - 建议状态流转")
    void testTbSuggestion() {
        TbSuggestion suggestion = new TbSuggestion();
        suggestion.setId(1L);
        suggestion.setUserId(6L);
        suggestion.setContent("增加活动场地");
        suggestion.setStatus("待处理");

        assertEquals(1L, suggestion.getId());
        assertEquals("增加活动场地", suggestion.getContent());
        assertEquals("待处理", suggestion.getStatus());

        suggestion.setStatus("已采纳");
        assertEquals("已采纳", suggestion.getStatus());

        suggestion.setStatus("已关闭");
        assertEquals("已关闭", suggestion.getStatus());
    }

    // ==================== TbActivityRegistration ====================

    @Test
    @DisplayName("TbActivityRegistration - 报名状态覆盖")
    void testTbActivityRegistration() {
        TbActivityRegistration reg = new TbActivityRegistration();
        reg.setId(1L);
        reg.setActivityId(1L);
        reg.setUserId(6L);
        reg.setStatus("待审核");

        assertEquals("待审核", reg.getStatus());

        reg.setStatus("已通过");
        assertEquals("已通过", reg.getStatus());

        reg.setStatus("已驳回");
        assertEquals("已驳回", reg.getStatus());
    }

    // ==================== SysRole ====================

    @Test
    @DisplayName("SysRole - 角色字段")
    void testSysRole() {
        SysRole role = new SysRole();
        role.setRoleId(1L);
        role.setRoleName("管理员");
        role.setRoleKey("admin");
        role.setPermissions("dashboard:view,club:manage,user:manage");
        role.setDeleted(0);

        assertEquals(1L, role.getRoleId());
        assertEquals("管理员", role.getRoleName());
        assertEquals("admin", role.getRoleKey());
        assertEquals("dashboard:view,club:manage,user:manage", role.getPermissions());
    }

    // ==================== SysMenu ====================

    @Test
    @DisplayName("SysMenu - 菜单树结构")
    void testSysMenu() {
        SysMenu menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setParentId(0L);
        menu.setMenuName("系统管理");
        menu.setMenuType("M");
        menu.setPermission(null);
        menu.setPath("/system");
        menu.setIcon("el-icon-setting");
        menu.setSort(1);
        menu.setStatus("0");
        menu.setDeleted(0);

        assertEquals(1L, menu.getMenuId());
        assertEquals(0L, menu.getParentId());
        assertEquals("系统管理", menu.getMenuName());
        assertEquals("M", menu.getMenuType());
        assertEquals("/system", menu.getPath());
        assertEquals("el-icon-setting", menu.getIcon());
        assertEquals(1, menu.getSort());
        assertEquals("0", menu.getStatus());
        assertNull(menu.getChildren());
    }

    @Test
    @DisplayName("SysMenu - 子菜单列表")
    void testSysMenuChildren() {
        SysMenu parent = new SysMenu();
        parent.setMenuId(1L);

        SysMenu child = new SysMenu();
        child.setMenuId(2L);
        child.setParentId(1L);
        child.setMenuName("用户列表");
        child.setMenuType("C");

        java.util.List<SysMenu> children = java.util.Arrays.asList(child);
        parent.setChildren(children);

        assertNotNull(parent.getChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(2L, parent.getChildren().get(0).getMenuId());
        assertEquals("用户列表", parent.getChildren().get(0).getMenuName());
    }

    // ==================== toString/equals ====================

    @Test
    @DisplayName("实体类 - toString不为空")
    void testToString() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUsername("admin");

        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("SysUser"), "toString should contain class name");
    }

    @Test
    @DisplayName("实体类 - 同ID实体equals比较")
    void testEquals() {
        SysUser user1 = new SysUser();
        user1.setUserId(1L);
        user1.setUsername("admin");

        SysUser user2 = new SysUser();
        user2.setUserId(1L);
        user2.setUsername("different_name");

        // Lombok @Data generates equals based on all fields
        // So two users with same fields will be equal
        SysUser user3 = new SysUser();
        user3.setUserId(1L);
        user3.setUsername("admin");

        assertEquals(user1, user3, "Same fields should be equal");
    }
}
