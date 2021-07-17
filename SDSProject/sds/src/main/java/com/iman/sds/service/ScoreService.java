package com.iman.sds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iman.sds.entity.Score;

import java.util.List;

public interface ScoreService extends IService<Score> {

    Long getFactoryIdByName(String factoryName);
    List<Score> getScoreByFacId(Long factoryId);

}
