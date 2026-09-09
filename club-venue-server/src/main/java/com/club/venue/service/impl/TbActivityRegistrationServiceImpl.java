package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbActivityRegistration;
import com.club.venue.mapper.TbActivityRegistrationMapper;
import com.club.venue.service.TbActivityRegistrationService;
import org.springframework.stereotype.Service;

@Service
public class TbActivityRegistrationServiceImpl
        extends ServiceImpl<TbActivityRegistrationMapper, TbActivityRegistration>
        implements TbActivityRegistrationService {
}
