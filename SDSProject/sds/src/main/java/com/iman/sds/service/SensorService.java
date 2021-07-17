package com.iman.sds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iman.sds.entity.Sensor;
import com.iman.sds.entity.SensorData;
import com.iman.sds.po.AddDataParam;
import com.iman.sds.po.AddLogParam;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
public interface SensorService extends IService<SensorData> {
    boolean saveSensorData(AddDataParam addDataParam);
    boolean saveSensorData2Chain(AddDataParam addDataParam);
    boolean saveLogData(AddLogParam addLogParam);
    boolean saveLogData2Chain(AddLogParam addLogParam);
    boolean saveScoreData(AddLogParam addLogParam);
    boolean saveSensor(Sensor sensor);
}
