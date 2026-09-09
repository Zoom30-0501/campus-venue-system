package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbActivity;
import com.club.venue.mapper.TbActivityMapper;
import com.club.venue.service.TbActivityService;
import org.springframework.stereotype.Service;

@Service
public class TbActivityServiceImpl extends ServiceImpl<TbActivityMapper, TbActivity> implements TbActivityService {
}
