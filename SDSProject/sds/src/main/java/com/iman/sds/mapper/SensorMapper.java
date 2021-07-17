package com.iman.sds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iman.sds.entity.ScDescription;
import com.iman.sds.entity.Score;
import com.iman.sds.entity.Sensor;
import com.iman.sds.entity.SensorData;
import com.iman.sds.po.AddDataParam;
import org.mapstruct.Mapper;

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
    boolean addSensorData2(AddDataParam addDataParam);
    boolean addScoreData2(Score score);
    boolean addLogData2(ScDescription scDescription);
    Sensor getSensorById2(Long id);
    Score getScoreById2(Long factoryId, Long sensorId);
    boolean addSensor2(Sensor sensor);
}
