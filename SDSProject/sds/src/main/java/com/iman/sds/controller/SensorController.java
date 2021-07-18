package com.iman.sds.controller;

import com.iman.sds.entity.ScDescription;
import com.iman.sds.entity.Sensor;
import com.iman.sds.entity.SensorData;
import com.iman.sds.entity.SensorInfo;
import com.iman.sds.po.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import com.iman.sds.common.ResponseMsg;
import com.iman.sds.service.SensorService;
import com.iman.sds.utils.JwtUtils;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        String hashCode = sensorService.saveSensorData2Chain(addDataParam);
        sensorService.saveSensorData(addDataParam);
        System.out.println(hashCode);
        Map<String, String> map = new HashMap<String, String>();
        map.put("hashCode", hashCode);
        return ResponseMsg.successResponse(map);
    }

    @RequestMapping(value = "/addLog", method = RequestMethod.POST)
    @RequiresPermissions(value = { "logdata:write" })
    @Transactional(rollbackFor = Exception.class)
    public ResponseMsg addLog(@RequestBody AddLogParam addLogParam){
        sensorService.saveScoreData(addLogParam);
        String hashCode = sensorService.saveLogData2Chain(addLogParam);
        sensorService.saveLogData(addLogParam);
        Map<String, String> map = new HashMap<String, String>();
        map.put("hashCode", hashCode);
        return ResponseMsg.successResponse(map);
    }

    /*@RequestMapping(value = "/addOne", method = RequestMethod.POST)
    @RequiresPermissions(value = { "sensor:add" })
    public ResponseMsg addSenor(@RequestBody Sensor sensor){

        return ResponseMsg.successResponse("OK");
    }*/

    @RequestMapping(value = "/queryData", method = RequestMethod.POST)
    public ResponseMsg queryData(@RequestBody QueryDataParam queryDataParam) throws ParseException {
        String factoryName = queryDataParam.getFactoryName();
        String address = queryDataParam.getAddress();
        String startTime1 = queryDataParam.getStartTime();
        String endTime1 = queryDataParam.getEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startTime = sdf.parse(startTime1);
        Date endTime = sdf.parse(endTime1);

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


    @RequestMapping(value = "/queryLog", method = RequestMethod.POST)
    public ResponseMsg queryLog(@RequestBody QueryLogParam queryLogParam) throws ParseException {
        String factoryName = queryLogParam.getFactoryName();
        String address = queryLogParam.getAddress();
        String startTime1 = queryLogParam.getStartTime();
        String endTime1 = queryLogParam.getEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startTime = sdf.parse(startTime1);
        Date endTime = sdf.parse(endTime1);
        List list = new ArrayList<LogDataReturn>();
        Map<String, List<LogDataParam>> result = sensorService.getLogDataByFacNameAndAddress(factoryName, address, startTime, endTime);

        for (String factoryName1 : result.keySet()) {
            LogDataReturn logDataReturn = new LogDataReturn();
            logDataReturn.setFactoryName(factoryName1);
            logDataReturn.setData(result.get(factoryName1));
            list.add(logDataReturn);
        }
//        Map map = new HashMap<String, List>();
//        map.put("result", list);
        return ResponseMsg.successResponse(list);
    }

}

