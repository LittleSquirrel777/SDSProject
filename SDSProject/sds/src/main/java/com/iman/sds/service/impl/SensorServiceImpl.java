package com.iman.sds.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iman.sds.entity.SensorData;
import com.iman.sds.entity.SensorInfo;
import com.iman.sds.mapper.SensorMapper;
import com.iman.sds.service.SensorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
@Service
public class SensorServiceImpl extends ServiceImpl<SensorMapper, SensorData> implements SensorService {

    @Override
    public Long getFactoryIdByName(String factoryName) {
        return null;
    }

    @Override
    public List<String> getScoreByFacId(Long factoryId) {
        return null;
    }

    @Override
    public List<SensorInfo> getSensorInfo() {
        return baseMapper.getSensorInfoList();
    }
}
