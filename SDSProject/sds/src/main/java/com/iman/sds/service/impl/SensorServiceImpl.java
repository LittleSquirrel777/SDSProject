package com.iman.sds.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iman.sds.chain.JRContractDemo;
import com.iman.sds.entity.*;
import com.iman.sds.mapper.SensorMapper;
import com.iman.sds.mapper.UserMapper;
import com.iman.sds.po.AddDataParam;
import com.iman.sds.po.AddLogParam;
import com.iman.sds.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import javax.annotation.PostConstruct;

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
    @Autowired
    SensorMapper sensorMapper;

    @Autowired
    UserMapper userMapper;

    JRContractDemo jRContractDemo;

    @PostConstruct
    public void init(){
        try {
            jRContractDemo = new JRContractDemo();
            //step 1:init mychain env.
            jRContractDemo.initMychainEnv();
            //step 2: init sdk client
            jRContractDemo.initSdk();
        } catch (Exception e) {
            jRContractDemo = null;
            e.printStackTrace();
        }
    }

    public boolean saveSensorData2Chain(AddDataParam addDataParam) {
        addDataParam.setCreateTime(new Date());
        SensorData sensorData = new SensorData();
        sensorData.setSensorId(addDataParam.getSensorId());
        sensorData.setAn(addDataParam.getAn());
        sensorData.setBod5(addDataParam.getBod5());
        sensorData.setChroma(addDataParam.getChroma());
        sensorData.setCod(addDataParam.getCod());
        sensorData.setPh(addDataParam.getPh());
        sensorData.setSs(addDataParam.getSs());
        sensorData.setTn(addDataParam.getTn());
        sensorData.setTp(addDataParam.getTp());
        sensorData.setToc(addDataParam.getToc());
        sensorData.setVp(addDataParam.getVp());
        sensorData.setCreteTime(addDataParam.getCreateTime());
        if(jRContractDemo != null){
            return jRContractDemo.callContractDataReceiveCredit(sensorMapper.getSensorById2(addDataParam.getSensorId()), sensorData);
        } else {
            return false ;
        }
    }

    @Override
    public boolean saveSensorData(AddDataParam addDataParam) {
        addDataParam.setCreateTime(new Date());
        return sensorMapper.addSensorData2(addDataParam);
    }

    public boolean saveLogData2Chain(AddLogParam addLogParam) {
        addLogParam.setCreateTime(new Date());
        Score score = new Score();
        score.setId(sensorMapper.getScoreById2(userMapper.getUserByName(addLogParam.getFactoryName()).getId(),addLogParam.getSensorId()).getId());
        score.setSensorId(addLogParam.getSensorId());
        score.setFactoryId(userMapper.getUserByName(addLogParam.getFactoryName()).getId());
        score.setNum(addLogParam.getNum());
        String operation;
        if(addLogParam.getNum() < 0) {
            operation = "-";
        } else {
            operation = "+";
        }
        ScDescription scDescription = new ScDescription();
        scDescription.setDescription(addLogParam.getDescription());
        scDescription.setCreateTime(addLogParam.getCreateTime());
        scDescription.setScoreId(sensorMapper.getScoreById2(userMapper.getUserByName(addLogParam.getFactoryName()).getId(),addLogParam.getSensorId()).getId());
        if(jRContractDemo != null){
            return jRContractDemo.callContractAddLogDataCredit(sensorMapper.getSensorById2(addLogParam.getSensorId()),score,operation,scDescription) ;
        } else {
            return false ;
        }
    }

    public boolean saveLogData(AddLogParam addLogParam) {
        addLogParam.setCreateTime(new Date());
        ScDescription scDescription = new ScDescription();
        scDescription.setDescription(addLogParam.getDescription());
        scDescription.setCreateTime(addLogParam.getCreateTime());
        scDescription.setScoreId(sensorMapper.getScoreById2(userMapper.getUserByName(addLogParam.getFactoryName()).getId(),addLogParam.getSensorId()).getId());
        return sensorMapper.addLogData2(scDescription);
    }

    public boolean saveScoreData(AddLogParam addLogParam) {
        Score score = new Score();
        score.setSensorId(addLogParam.getSensorId());
        score.setFactoryId(userMapper.getUserByName(addLogParam.getFactoryName()).getId());
        score.setNum(addLogParam.getNum());
        return sensorMapper.addScoreData2(score);
    }

    @Override
    public Map<String, List<SensorData>> getSensorDataByFacNameAndAddress(String factoryName, String address, Date startTime, Date endTime) {
        List<Sensor> result1 = null;
        List<Sensor> result2 = null;
        List<Sensor> result = null;
        if (factoryName != null) {
            //根据工厂名字获得工厂id
            Long factoryId = this.baseMapper.getFacIdByFacName(factoryName);
            //根据工厂id获得对应的sensorId
            result1 = this.baseMapper.getSensorIdByFacId(factoryId);
        }
        if (address != null) {
            result2 = this.baseMapper.getSensorIdBySenAddress(address);
        }

        if (result1 != null && result2 != null) {
//            Set<Sensor> set = new HashSet<Sensor>();
//            for (int i = 0; i < result1.size(); i++) {
//                set.add(result1.get(i));
//            }
//            for (int i = 0; i < result2.size(); i++) {
//                set.add(result2.get(i));
//            }
            result1.retainAll(result2);
            result = result1;
        } else if (result1 != null) {
            result = result1;
        } else if (result2 != null) {
            result = result2;
        }
        if (result == null) {
            result = this.baseMapper.getAllSensor();
        }
        Map<String, List<SensorData>> map = new HashMap<String, List<SensorData>>();
        if (startTime != null && endTime != null) {
            for (int i = 0; i < result.size(); i++) {
                List<SensorData> sensorData = JRContractDemo.dataToSensorDataList2(result.get(i), startTime, endTime);
                String factoryName1 = this.baseMapper.getFacNameBySensorId(sensorData.get(i).getId());
                map.put(factoryName1, sensorData);
            }
        } else {
            for (int i = 0; i < result.size(); i++) {
                List<SensorData> sensorData = JRContractDemo.dataToSensorDataList1(result.get(i));
                String factoryName1 = this.baseMapper.getFacNameBySensorId(sensorData.get(i).getId());
                map.put(factoryName1, sensorData);
            }
        }
        return map;
//        return this.baseMapper.selectSensorIdByFacNameAndAddress(factoryName, address);
    }

    public boolean saveSensor(Sensor sensor) {
        return sensorMapper.addSensor2(sensor);
    }
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
