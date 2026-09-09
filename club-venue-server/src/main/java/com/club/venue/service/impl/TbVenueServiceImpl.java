package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbVenue;
import com.club.venue.mapper.TbVenueMapper;
import com.club.venue.service.TbVenueService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbVenueServiceImpl extends ServiceImpl<TbVenueMapper, TbVenue> implements TbVenueService {

    @Override
    @Cacheable(value = "venues", key = "'list'")
    public List<TbVenue> list() {
        return super.list();
    }

    @Override
    @CacheEvict(value = "venues", allEntries = true)
    public boolean save(TbVenue entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "venues", allEntries = true)
    public boolean updateById(TbVenue entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "venues", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
