package com.iman.sds.controller;

import com.iman.sds.common.ResponseMsg;
import com.iman.sds.entity.Score;
import com.iman.sds.po.ListScoreParam;
import com.iman.sds.service.ScoreService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreController extends BaseController{

    @Autowired
    ScoreService scoreService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @RequiresPermissions(value = { "AllScore:view" })
    public ResponseMsg getAllScore(@RequestBody ListScoreParam listScoreParam){
        String factoryName = listScoreParam.getFactoryName();
        if (factoryName.length() == 0) {
            List<Score> list = scoreService.getScoreById();
            Map result = new HashMap();
            result.put("scoreList", list);
            return ResponseMsg.successResponse(result);
        } else {
            Long factoryId = scoreService.getFactoryIdByName(factoryName);
            List<Score> list = scoreService.getScoreByFacId(factoryId);
            Map result = new HashMap();
            result.put("scoreList", list);
            return ResponseMsg.successResponse(result);
        }
    }

}
