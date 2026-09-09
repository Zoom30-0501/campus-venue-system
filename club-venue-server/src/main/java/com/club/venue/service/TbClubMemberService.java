package com.club.venue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.venue.entity.TbClubMember;

import java.util.List;

public interface TbClubMemberService extends IService<TbClubMember> {
    List<TbClubMember> getMembersByClubId(Long clubId);
}
