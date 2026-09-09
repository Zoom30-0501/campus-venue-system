package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbClubApplication;
import com.club.venue.mapper.TbClubApplicationMapper;
import com.club.venue.service.TbClubApplicationService;
import org.springframework.stereotype.Service;

@Service
public class TbClubApplicationServiceImpl extends ServiceImpl<TbClubApplicationMapper, TbClubApplication> implements TbClubApplicationService {
}
