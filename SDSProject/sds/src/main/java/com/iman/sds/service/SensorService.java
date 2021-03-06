package com.iman.sds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iman.sds.entity.*;
import com.iman.sds.entity.SensorData;
import com.iman.sds.po.AddDataParam;
import com.iman.sds.po.AddLogParam;
import com.iman.sds.po.LogDataParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    boolean saveSensorData(AddDataParam addDataParam);
    String saveSensorData2Chain(AddDataParam addDataParam);
    boolean saveLogData(AddLogParam addLogParam);
    String saveLogData2Chain(AddLogParam addLogParam);
    boolean saveScoreData(AddLogParam addLogParam);
    List<ScDescription> listLog(String factoryName);
    //List<ScDescription> listLogFromChain(String factoryName);

    Map<String, List<SensorData>> getSensorDataByFacNameAndAddress(String factoryName, String address, Date startTime, Date endTime);
    Map<String, List<LogDataParam>> getLogDataByFacNameAndAddress(String factoryName, String address, Date startTime, Date endTime);
}
