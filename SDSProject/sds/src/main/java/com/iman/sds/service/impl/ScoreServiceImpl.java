package com.iman.sds.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iman.sds.entity.Score;
import com.iman.sds.mapper.ScoreMapper;
import com.iman.sds.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getFactoryIdByName(String factoryName) {
        return this.baseMapper.searchIdByName(factoryName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Score> getScoreByFacId(Long factoryId) {
        return this.baseMapper.getScoreById(factoryId);
    }


}
