package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbClubMember;
import com.club.venue.mapper.TbClubMemberMapper;
import com.club.venue.service.TbClubMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbClubMemberServiceImpl extends ServiceImpl<TbClubMemberMapper, TbClubMember> implements TbClubMemberService {

    @Override
    public List<TbClubMember> getMembersByClubId(Long clubId) {
        return lambdaQuery().eq(TbClubMember::getClubId, clubId).list();
    }
}
