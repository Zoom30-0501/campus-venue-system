package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbClub;
import com.club.venue.mapper.TbClubMapper;
import com.club.venue.service.TbClubService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbClubServiceImpl extends ServiceImpl<TbClubMapper, TbClub> implements TbClubService {

    @Override
    @Cacheable(value = "clubs", key = "'list'")
    public List<TbClub> list() {
        return super.list();
    }

    @Override
    @CacheEvict(value = "clubs", allEntries = true)
    public boolean save(TbClub entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "clubs", allEntries = true)
    public boolean updateById(TbClub entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "clubs", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
