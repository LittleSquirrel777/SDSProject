package com.iman.sds.controller;

import com.iman.sds.entity.ScDescription;
import com.iman.sds.entity.Sensor;
import com.iman.sds.entity.SensorData;
import com.iman.sds.entity.SensorInfo;
import com.iman.sds.po.AddLogParam;
import com.iman.sds.po.QueryDataParam;
import com.iman.sds.po.QueryLogParam;
import com.iman.sds.po.SensorDataReturn;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import com.iman.sds.common.ResponseMsg;
import com.iman.sds.po.AddDataParam;
import com.iman.sds.service.SensorService;
import com.iman.sds.utils.JwtUtils;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
@RestController
@RequestMapping("/api/sensor")

//智能合约的接口

//private static boolean callContractAddSensorCredit(Integer name,String addr)
//添加一个传感器，需要参数(传感器的id，传感器的地址)
//private static boolean callContractDataReceiveCredit(Sensor sensor, SensorData water)
//上传传感器的一个数据到链上 参数（sensor，water）
//public static List<SensorData> dataToSensorDataList(Sensor sensor)
//给一个sensor，返回一个传感器的水数据的列表
// private static boolean callContractAddLogDataCredit(Sensor sensor, Score score, String operation, ScDescription scDescription)
//上传传感器的一个日志数据  参数（sensor，score，操作符 “+” “-”，scDescription 描述）
//public static List<Map<String, String>> logDataToMap(Sensor sensor)
//给一个sensor 返回这个传感器的日志数据（"sensorId": "", "address": "", "description": ""）
public class SensorController {
    @Autowired
    SensorService sensorService;
    /*
    获取全部传感器
    * */
    @RequestMapping(value = "/listSensor", method = RequestMethod.GET)
    @RequiresPermissions(value = { "sensor:view" })
    public ResponseMsg getAllSensorData(){
        List<SensorInfo> list = sensorService.getSensorInfo();
        Map result = new HashMap();
        result.put("sensors", list);
        return ResponseMsg.successResponse(result);
    }

    @RequestMapping(value = "/addData", method = RequestMethod.POST)
    @RequiresPermissions(value = { "sensordata:add" })
    public ResponseMsg addData(@RequestBody AddDataParam addDataParam) {
        sensorService.saveSensorData2Chain(addDataParam);
        sensorService.saveSensorData(addDataParam);
        return ResponseMsg.successResponse("OK");
    }

    @RequestMapping(value = "/addLog", method = RequestMethod.POST)
    @RequiresPermissions(value = { "logdata:write" })
    @Transactional(rollbackFor = Exception.class)
    public ResponseMsg addLog(@RequestBody AddLogParam addLogParam){
        sensorService.saveScoreData(addLogParam);
        sensorService.saveLogData2Chain(addLogParam);
        sensorService.saveLogData(addLogParam);
        return ResponseMsg.successResponse("OK");
    }

    @RequestMapping(value = "/addOne", method = RequestMethod.POST)
    @RequiresPermissions(value = { "sensor:add" })
    public ResponseMsg addSenor(@RequestBody Sensor sensor){

        return ResponseMsg.successResponse("OK");
    }

    @RequestMapping(value = "/queryData", method = RequestMethod.GET)
    public ResponseMsg queryData(@RequestBody QueryDataParam queryDataParam) {
        String factoryName = queryDataParam.getFactoryName();
        String address = queryDataParam.getAddress();
        Date startTime = queryDataParam.getStartTime();
        Date endTime = queryDataParam.getEndTime();
//        Map hashmap = new HashMap<>();
//        hashmap.put("factoryName", factoryName);
//        hashmap.put()
        List<SensorDataReturn> list = new ArrayList<SensorDataReturn>();
        Map<String, List<SensorData>> sensorData = sensorService.getSensorDataByFacNameAndAddress(factoryName, address, startTime, endTime);
        for (String factoryName1 : sensorData.keySet()) {
            SensorDataReturn tmp = new SensorDataReturn();
            tmp.setFactoryName(factoryName1);
            List<SensorData> sensorData1 = sensorData.get(factoryName1);
            tmp.setData(sensorData1);
            list.add(tmp);
        }
        return ResponseMsg.successResponse(list);
    }

    /*
    @RequestMapping(value = "/queryLog", method = RequestMethod.GET)
    public ResponseMsg queryLog(@RequestBody QueryLogParam queryLogParam){
        String factoryName = queryLogParam.getFactoryName();
        String address = queryLogParam.getAddress();
        Date startTime = queryLogParam.getStartTime();
        Date endTime = queryLogParam.getEndTime();
        Map<String, List<ScDescription>> scDescriptions = sensorService.getLogDataByFacNameAndAddress(factoryName, address, startTime, endTime);

        return ResponseMsg.successResponse(scDescriptions);
    }
     */
}

