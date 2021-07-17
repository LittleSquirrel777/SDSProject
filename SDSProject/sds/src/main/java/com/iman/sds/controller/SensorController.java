package com.iman.sds.controller;


import com.iman.sds.common.ResponseMsg;
import com.iman.sds.entity.SensorInfo;
import com.iman.sds.service.SensorService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class SensorController extends BaseController{
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
}

