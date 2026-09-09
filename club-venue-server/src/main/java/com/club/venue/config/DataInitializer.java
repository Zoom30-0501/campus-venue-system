package com.club.venue.config;

import com.club.venue.entity.*;
import com.club.venue.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private SysRoleMapper sysRoleMapper;
    @Autowired private TbClubMapper tbClubMapper;
    @Autowired private TbClubMemberMapper tbClubMemberMapper;
    @Autowired private TbVenueMapper tbVenueMapper;
    @Autowired private TbFacilityMapper tbFacilityMapper;
    @Autowired private TbActivityMapper tbActivityMapper;
    @Autowired private TbActivitySummaryMapper tbActivitySummaryMapper;
    @Autowired private TbActivityRatingMapper tbActivityRatingMapper;
    @Autowired private TbCheckInMapper tbCheckInMapper;
    @Autowired private TbVenueScheduleMapper tbVenueScheduleMapper;
    @Autowired private TbSuggestionMapper tbSuggestionMapper;
    @Autowired private TbClubAlbumMapper tbClubAlbumMapper;
    @Autowired private TbClubApplicationMapper tbClubApplicationMapper;
    @Autowired private TbActivityRegistrationMapper tbActivityRegistrationMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Skip if data already exists
        Long userCount = sysUserMapper.selectCount(null);
        if (userCount != null && userCount > 0) {
            System.out.println("Test data already exists, skipping initialization.");
            return;
        }
        System.out.println("=== Initializing test data ===");

        // === Users ===
        SysUser admin = createUser("admin", "管理员", "admin", null, null);
        SysUser leader1 = createUser("zhangsan", "张三", "club_leader", 1L, "计算机协会");
        SysUser leader2 = createUser("lisi", "李四", "club_leader", 2L, "篮球社");
        SysUser leader3 = createUser("wangwu", "王五", "club_leader", 3L, "音乐社");
        SysUser leader4 = createUser("zhaoliu", "赵六", "club_leader", 4L, "志愿者协会");
        SysUser user1 = createUser("testuser", "测试用户", "user", null, null);
        SysUser user2 = createUser("sunqi", "孙七", "user", null, null);
        SysUser user3 = createUser("zhouba", "周八", "user", null, null);
        SysUser user4 = createUser("wujiu", "吴九", "user", null, null);
        SysUser user5 = createUser("zhengshi", "郑十", "user", null, null);

        // === Roles ===
        createRole("管理员", "admin", "dashboard:view,club:manage,venue:manage,activity:manage,facility:manage,user:manage,role:manage,suggestion:manage");
        createRole("社团负责人", "club_leader", "dashboard:view,club:manage,venue:view,activity:manage,checkin:manage");
        createRole("普通用户", "user", "dashboard:view,club:view,venue:view,activity:view,checkin:create");

        // === Clubs ===
        TbClub club1 = createClub("计算机协会", "学术科技", leader1.getUserId(), "张三", "13800001111");
        TbClub club2 = createClub("篮球社", "体育竞技", leader2.getUserId(), "李四", "13800002222");
        TbClub club3 = createClub("音乐社", "文化艺术", leader3.getUserId(), "王五", "13800003333");
        TbClub club4 = createClub("志愿者协会", "公益实践", leader4.getUserId(), "赵六", "13800004444");

        // Update user club assignments
        updateUserClub(user1.getUserId(), club1.getClubId(), club1.getClubName());
        updateUserClub(user2.getUserId(), club2.getClubId(), club2.getClubName());
        updateUserClub(user3.getUserId(), club3.getClubId(), club3.getClubName());
        updateUserClub(user4.getUserId(), club4.getClubId(), club4.getClubName());
        updateUserClub(user5.getUserId(), club1.getClubId(), club1.getClubName());

        // === Club Members ===
        createMember(club1.getClubId(), leader1.getUserId(), "负责人");
        createMember(club1.getClubId(), user1.getUserId(), "成员");
        createMember(club1.getClubId(), user5.getUserId(), "成员");
        createMember(club2.getClubId(), leader2.getUserId(), "负责人");
        createMember(club2.getClubId(), user2.getUserId(), "副负责人");
        createMember(club3.getClubId(), leader3.getUserId(), "负责人");
        createMember(club3.getClubId(), user3.getUserId(), "成员");
        createMember(club4.getClubId(), leader4.getUserId(), "负责人");
        createMember(club4.getClubId(), user4.getUserId(), "成员");

        // === Club Albums ===
        createAlbum(club1.getClubId(), "https://picsum.photos/seed/comp1/400/300");
        createAlbum(club1.getClubId(), "https://picsum.photos/seed/comp2/400/300");
        createAlbum(club2.getClubId(), "https://picsum.photos/seed/basket1/400/300");
        createAlbum(club3.getClubId(), "https://picsum.photos/seed/music1/400/300");

        // === Venues ===
        TbVenue venue1 = createVenue("多媒体教室A201", "R001", "可用");
        TbVenue venue2 = createVenue("大学生活动中心多功能厅", "R002", "可用");
        TbVenue venue3 = createVenue("室内体育馆", "R003", "可用");
        TbVenue venue4 = createVenue("学术报告厅", "R004", "可用");
        TbVenue venue5 = createVenue("室外篮球场", "R005", "可用");
        TbVenue venue6 = createVenue("音乐排练厅", "R006", "维护中");

        // === Facilities ===
        createFacility("投影仪", "正常", venue1.getVenueId());
        createFacility("音响系统", "正常", venue1.getVenueId());
        createFacility("舞台灯光", "正常", venue2.getVenueId());
        createFacility("LED大屏", "正常", venue2.getVenueId());
        createFacility("篮球架", "正常", venue3.getVenueId());
        createFacility("计分板", "维修中", venue3.getVenueId());
        createFacility("讲台", "正常", venue4.getVenueId());
        createFacility("投影幕布", "正常", venue4.getVenueId());
        createFacility("篮球框", "正常", venue5.getVenueId());
        createFacility("钢琴", "正常", venue6.getVenueId());
        createFacility("调音台", "损坏", venue6.getVenueId());

        // === Activities ===
        TbActivity act1 = createActivity("计算机编程大赛", "举办全校程序设计竞赛，提升同学编程能力",
                LocalDateTime.of(2026, 6, 10, 9, 0), LocalDateTime.of(2026, 6, 10, 17, 0),
                leader1.getUserId(), club1.getClubId(), venue1.getVenueId(), "已通过");
        TbActivity act2 = createActivity("迎新篮球赛", "组织新生篮球友谊赛",
                LocalDateTime.of(2026, 6, 15, 14, 0), LocalDateTime.of(2026, 6, 15, 18, 0),
                leader2.getUserId(), club2.getClubId(), venue3.getVenueId(), "已通过");
        TbActivity act3 = createActivity("校园音乐节", "年度校园音乐节演出",
                LocalDateTime.of(2026, 7, 1, 18, 0), LocalDateTime.of(2026, 7, 3, 22, 0),
                leader3.getUserId(), club3.getClubId(), venue2.getVenueId(), "一级审核通过");
        TbActivity act4 = createActivity("志愿者培训大会", "新学期志愿者工作培训",
                LocalDateTime.of(2026, 6, 20, 9, 0), LocalDateTime.of(2026, 6, 20, 12, 0),
                leader4.getUserId(), club4.getClubId(), venue4.getVenueId(), "已通过");
        TbActivity act5 = createActivity("AI技术分享会", "邀请企业导师分享AI前沿技术",
                LocalDateTime.of(2026, 6, 25, 19, 0), LocalDateTime.of(2026, 6, 25, 21, 0),
                leader1.getUserId(), club1.getClubId(), venue1.getVenueId(), "待审核");
        TbActivity act6 = createActivity("篮球技巧培训", "专业教练指导篮球基础技巧",
                LocalDateTime.of(2026, 6, 28, 15, 0), LocalDateTime.of(2026, 6, 28, 17, 0),
                leader2.getUserId(), club2.getClubId(), venue5.getVenueId(), "待审核");
        TbActivity act7 = createActivity("吉他入门教学", "吉他基础教学课程",
                LocalDateTime.of(2026, 6, 18, 14, 0), LocalDateTime.of(2026, 6, 18, 16, 0),
                leader3.getUserId(), club3.getClubId(), venue6.getVenueId(), "已驳回");
        TbActivity act8 = createActivity("社区义务维修电脑", "为社区居民免费维修电脑",
                LocalDateTime.of(2026, 7, 5, 8, 0), LocalDateTime.of(2026, 7, 5, 17, 0),
                leader1.getUserId(), club1.getClubId(), venue1.getVenueId(), "已通过");

        // === Activity Summaries ===
        createSummary(act1.getActivityId(), "本次编程大赛共有85名同学参赛，评选出一等奖1名、二等奖3名、三等奖5名。活动圆满成功，同学们反响热烈。", 85, "希望下次增加更多题目类型");
        createSummary(act4.getActivityId(), "培训大会顺利开展，共有60名志愿者参加，对志愿服务流程和注意事项进行了详细讲解。", 60, "内容充实，时间略长");

        // === Activity Ratings ===
        createRating(act1.getActivityId(), user1.getUserId(), 5, "非常棒的比赛，学到了很多！");
        createRating(act1.getActivityId(), user2.getUserId(), 4, "组织得很好，题目有挑战性");
        createRating(act2.getActivityId(), user3.getUserId(), 5, "篮球赛太精彩了！");
        createRating(act4.getActivityId(), user4.getUserId(), 4, "培训很有收获");
        createRating(act4.getActivityId(), user1.getUserId(), 3, "希望增加互动环节");

        // === Check-ins ===
        createCheckIn(act1.getActivityId(), user1.getUserId());
        createCheckIn(act1.getActivityId(), user2.getUserId());
        createCheckIn(act1.getActivityId(), user5.getUserId());
        createCheckIn(act2.getActivityId(), user3.getUserId());
        createCheckIn(act2.getActivityId(), leader2.getUserId());
        createCheckIn(act4.getActivityId(), user4.getUserId());
        createCheckIn(act4.getActivityId(), leader4.getUserId());
        createCheckIn(act4.getActivityId(), user1.getUserId());

        // === Venue Schedules ===
        createSchedule(venue1.getVenueId(), act1.getActivityId(), act1.getActivityName(), act1.getStartTime(), act1.getEndTime());
        createSchedule(venue3.getVenueId(), act2.getActivityId(), act2.getActivityName(), act2.getStartTime(), act2.getEndTime());
        createSchedule(venue4.getVenueId(), act4.getActivityId(), act4.getActivityName(), act4.getStartTime(), act4.getEndTime());
        createSchedule(venue1.getVenueId(), act8.getActivityId(), act8.getActivityName(), act8.getStartTime(), act8.getEndTime());

        // === Suggestions ===
        createSuggestion(user1.getUserId(), "希望学校能增加更多的活动场地，特别是室内排练厅数量不足。");
        createSuggestion(user2.getUserId(), "篮球场地面有些破损，希望能及时修缮，避免运动损伤。");
        createSuggestion(user3.getUserId(), "建议在场地申请系统中增加在线支付功能，方便缴纳场地押金。");
        createSuggestion(user4.getUserId(), "志愿者活动审批流程希望能简化，目前等待时间较长。");

        // === Club Applications ===
        createApplication(club1.getClubId(), user2.getUserId(), "我对计算机技术非常感兴趣，希望能加入计算机协会学习和交流。");

        // === Activity Registrations (活动报名) ===
        createRegistration(act1.getActivityId(), user1.getUserId(), "已通过");
        createRegistration(act1.getActivityId(), user2.getUserId(), "已通过");
        createRegistration(act2.getActivityId(), user3.getUserId(), "待审核");
        createRegistration(act4.getActivityId(), user4.getUserId(), "已通过");
        createRegistration(act5.getActivityId(), user5.getUserId(), "待审核");

        System.out.println("=== Test data initialization complete ===");
    }

    private SysUser createUser(String username, String nickname, String role, Long clubId, String clubName) {
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setNickname(nickname);
        u.setPassword(passwordEncoder.encode("123456"));
        u.setRole(role);
        u.setClubId(clubId);
        u.setClubName(clubName);
        u.setDeleted(0);
        sysUserMapper.insert(u);
        System.out.println("  Created user: " + username + " (id=" + u.getUserId() + ")");
        return u;
    }

    private void updateUserClub(Long userId, Long clubId, String clubName) {
        SysUser u = sysUserMapper.selectById(userId);
        if (u != null) {
            u.setClubId(clubId);
            u.setClubName(clubName);
            sysUserMapper.updateById(u);
        }
    }

    private void createRole(String name, String key, String permissions) {
        SysRole r = new SysRole();
        r.setRoleName(name);
        r.setRoleKey(key);
        r.setPermissions(permissions);
        r.setDeleted(0);
        sysRoleMapper.insert(r);
    }

    private TbClub createClub(String name, String category, Long leaderId, String leaderName, String phone) {
        TbClub c = new TbClub();
        c.setClubName(name);
        c.setCategory(category);
        c.setLeaderId(leaderId);
        c.setLeaderName(leaderName);
        c.setPhone(phone);
        c.setDeleted(0);
        tbClubMapper.insert(c);
        System.out.println("  Created club: " + name + " (id=" + c.getClubId() + ")");
        return c;
    }

    private void createMember(Long clubId, Long userId, String role) {
        TbClubMember m = new TbClubMember();
        m.setClubId(clubId);
        m.setUserId(userId);
        m.setRole(role);
        m.setDeleted(0);
        tbClubMemberMapper.insert(m);
    }

    private void createAlbum(Long clubId, String url) {
        TbClubAlbum a = new TbClubAlbum();
        a.setClubId(clubId);
        a.setImageUrl(url);
        a.setDeleted(0);
        tbClubAlbumMapper.insert(a);
    }

    private TbVenue createVenue(String name, String no, String status) {
        TbVenue v = new TbVenue();
        v.setVenueName(name);
        v.setVenueNo(no);
        v.setStatus(status);
        v.setDeleted(0);
        tbVenueMapper.insert(v);
        return v;
    }

    private void createFacility(String name, String status, Long venueId) {
        TbFacility f = new TbFacility();
        f.setFacilityName(name);
        f.setStatus(status);
        f.setVenueId(venueId);
        f.setDeleted(0);
        tbFacilityMapper.insert(f);
    }

    private TbActivity createActivity(String name, String reason, LocalDateTime start, LocalDateTime end,
                                       Long applicantId, Long clubId, Long venueId, String status) {
        TbActivity a = new TbActivity();
        a.setActivityName(name);
        a.setReason(reason);
        a.setStartTime(start);
        a.setEndTime(end);
        a.setApplicantId(applicantId);
        a.setClubId(clubId);
        a.setVenueId(venueId);
        a.setStatus(status);
        a.setDeleted(0);
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(LocalDateTime.now());
        tbActivityMapper.insert(a);
        System.out.println("  Created activity: " + name + " (id=" + a.getActivityId() + ")");
        return a;
    }

    private void createSummary(Long activityId, String content, int count, String feedback) {
        TbActivitySummary s = new TbActivitySummary();
        s.setActivityId(activityId);
        s.setContent(content);
        s.setParticipantCount(count);
        s.setFeedback(feedback);
        s.setDeleted(0);
        tbActivitySummaryMapper.insert(s);
    }

    private void createRating(Long activityId, Long userId, int score, String comment) {
        TbActivityRating r = new TbActivityRating();
        r.setActivityId(activityId);
        r.setUserId(userId);
        r.setScore(score);
        r.setComment(comment);
        r.setDeleted(0);
        tbActivityRatingMapper.insert(r);
    }

    private void createCheckIn(Long activityId, Long userId) {
        TbCheckIn c = new TbCheckIn();
        c.setActivityId(activityId);
        c.setUserId(userId);
        c.setCheckInTime(LocalDateTime.now());
        c.setVerified(1);
        tbCheckInMapper.insert(c);
    }

    private void createSchedule(Long venueId, Long activityId, String title, LocalDateTime start, LocalDateTime end) {
        TbVenueSchedule s = new TbVenueSchedule();
        s.setVenueId(venueId);
        s.setActivityId(activityId);
        s.setTitle(title);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setStatus("已锁定");
        tbVenueScheduleMapper.insert(s);
    }

    private void createSuggestion(Long userId, String content) {
        TbSuggestion s = new TbSuggestion();
        s.setUserId(userId);
        s.setContent(content);
        s.setStatus("待处理");
        tbSuggestionMapper.insert(s);
    }

    private void createApplication(Long clubId, Long userId, String reason) {
        TbClubApplication a = new TbClubApplication();
        a.setClubId(clubId);
        a.setUserId(userId);
        a.setReason(reason);
        a.setStatus("待审核");
        a.setDeleted(0);
        tbClubApplicationMapper.insert(a);
    }

    private void createRegistration(Long activityId, Long userId, String status) {
        TbActivityRegistration r = new TbActivityRegistration();
        r.setActivityId(activityId);
        r.setUserId(userId);
        r.setStatus(status);
        tbActivityRegistrationMapper.insert(r);
    }
}
