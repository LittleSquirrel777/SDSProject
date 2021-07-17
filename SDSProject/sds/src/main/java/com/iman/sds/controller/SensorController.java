package com.iman.sds.controller;


import com.iman.sds.entity.Sensor;
import com.iman.sds.service.SensorService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
@Controller
@RequestMapping("/api/sensor")

//智能合约的接口

//private static boolean callContractAddSensorCredit(Integer name,String addr)
//添加一个传感器，需要参数(传感器的id，传感器的地址)
//private static boolean callContractDataReceiveCredit(Sensor sensor, SensorData water)
//参数（sensor，water）  上传传感器的一个数据到链上
//private static String callContractDataQueryCredit(Sensor sensor)
//参数（sensor）    根据传感器的信息，返回传感器对应的所有水数据
// private static boolean callContractAddLogDataCredit(Sensor sensor, Score score, String operation, ScDescription scDescription)
//增加一个日志数据  参数（sensor，score，操作符 “+” “-”，scDescription 描述）
// private static String callContractQueryLogDataCredit(Sensor sensor)
//调用合约差一条记录 参数（sensor）
public class SensorController extends BaseController{
    @Autowired
    SensorService sensorService;
    /*
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @RequiresPermissions(value = {"facwater:view"})
    private static String callContractDataQueryCredit(Sensor sensor){
        List<> list = sensorService.list();
    }

     */
}

