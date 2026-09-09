package com.club.venue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.venue.entity.TbFacility;
import com.club.venue.mapper.TbFacilityMapper;
import com.club.venue.service.TbFacilityService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbFacilityServiceImpl extends ServiceImpl<TbFacilityMapper, TbFacility> implements TbFacilityService {

    @Override
    @Cacheable(value = "facilities", key = "'list'")
    public List<TbFacility> list() {
        return super.list();
    }

    @Override
    @CacheEvict(value = "facilities", allEntries = true)
    public boolean save(TbFacility entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "facilities", allEntries = true)
    public boolean updateById(TbFacility entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "facilities", allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}
