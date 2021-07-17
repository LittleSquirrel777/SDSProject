package com.iman.sds.controller;


import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chris
 * @date 2021/7/12 23:06
 * @Email:gem7991@dingtalk.com
 */

@RestController
@RequestMapping("/api/logistic")
public class LogisticController extends BaseController{
//    @Autowired
//    LogisticService logisticService;
//
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @RequiresPermissions(value = { "logistic:view" })
//    public ResponseMsg getAllLoginstic(){
//        List<LogisticInfo> list = logisticService.list();
//        Map result = new HashMap();
//        result.put("logistics", list);
//        return ResponseMsg.successResponse(result);
//    }
}
