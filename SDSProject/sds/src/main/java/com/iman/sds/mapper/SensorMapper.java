package com.iman.sds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iman.sds.entity.SensorData;
import com.iman.sds.entity.SensorInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
@Mapper
public interface SensorMapper extends BaseMapper<SensorData> {
    List<SensorInfo> getSensorInfoList();
}
