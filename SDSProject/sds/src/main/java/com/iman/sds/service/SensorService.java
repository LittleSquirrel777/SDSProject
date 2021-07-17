package com.iman.sds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iman.sds.entity.Score;
import com.iman.sds.entity.SensorData;
import com.iman.sds.entity.SensorInfo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
public interface SensorService extends IService<SensorData> {
    Long getFactoryIdByName(String factoryName);
    List<String> getScoreByFacId(Long factoryId);
    List<SensorInfo> getSensorInfo();
}
