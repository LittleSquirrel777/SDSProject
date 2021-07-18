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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import java.util.Date;

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
        score.setId(sensorMapper.getScoreById2(userMapper.getUserInfoByName2(addLogParam.getFactoryName()).getUserId(),addLogParam.getSensorId()).getId());
        score.setSensorId(addLogParam.getSensorId());
        score.setFactoryId(userMapper.getUserInfoByName2(addLogParam.getFactoryName()).getUserId());
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
        scDescription.setScoreId(sensorMapper.getScoreById2(userMapper.getUserInfoByName2(addLogParam.getFactoryName()).getUserId(),addLogParam.getSensorId()).getId());
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
        scDescription.setScoreId(sensorMapper.getScoreById2(userMapper.getUserInfoByName2(addLogParam.getFactoryName()).getUserId(),addLogParam.getSensorId()).getId());
        return sensorMapper.addLogData2(scDescription);
    }

    public boolean saveScoreData(AddLogParam addLogParam) {
        Score score = new Score();
        score.setSensorId(addLogParam.getSensorId());
        score.setFactoryId(userMapper.getUserInfoByName2(addLogParam.getFactoryName()).getUserId());
        score.setNum(addLogParam.getNum());
        return sensorMapper.addScoreData2(score);
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

    public List<ScDescription> listLog(String factoryName) {
        Long factoryId = userMapper.getUserByName(factoryName).getId(); //根据工厂名得到工厂id
        List<Long> scoreIds = sensorMapper.getScoreIdsByFactoryId2(factoryId); //根据工厂id得到积分记录id
        List<ScDescription> scDescriptions = new ArrayList<>();
        for (int i = 0; i < scoreIds.size(); i ++) {
            scDescriptions.add(sensorMapper.getScDescriptionById2(scoreIds.get(i)));
        }
        return scDescriptions;
    };
}
