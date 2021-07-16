package com.iman.sds.controller;

import com.iman.sds.common.ResponseMsg;
import com.iman.sds.common.SalixError;
import com.iman.sds.common.log.SalixLog;
import com.iman.sds.entity.User;
import com.iman.sds.entity.UserInfo;
import com.iman.sds.entity.UserLoginParam;
import com.iman.sds.entity.UserRegisterParam;
import com.iman.sds.entity.UserRole;
import com.iman.sds.service.UserService;
import com.iman.sds.service.impl.CurrentUserService;
import com.iman.sds.utils.JwtUtils;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris
 * @date 2021/7/12 21:48
 * @Email:gem7991@dingtalk.com
 */

@Api(value = "Login", tags = "login 登录验证")
@CrossOrigin(maxAge = 3600000)
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    private final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseMsg login(@RequestBody UserLoginParam userLoginParam){
        SalixLog salixLog = new SalixLog();
        salixLog.add("account", userLoginParam.getAccount());
        salixLog.add("password", userLoginParam.getPassword());
        logger.info(salixLog.toString());
        if(userLoginParam.getAccount() == null || userLoginParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }

        userService.authentic(userLoginParam);
        if(!CurrentUserService.getUser().isAuthenticated()) {
            return ResponseMsg.errorResponse(SalixError.MSG_USER_VERIFY_ERROR);
        }

        User user = CurrentUserService.getUser();
        String jwtToken = JwtUtils.sign(user.getName(), JwtUtils.SECRET);
        Map<String, Object> result = new HashMap<>();
        result.put("token",jwtToken);

        logger.info(user.getName() +"登录");

        return ResponseMsg.successResponse(result);
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMsg login(@RequestBody UserRegisterParam userRegisterParam){
        if(userRegisterParam.getAccount() == null || userRegisterParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }

        User newUser = new User();
        newUser.setSalt(JwtUtils.generateSalt());
        String code = userRegisterParam.getAccount().concat(userRegisterParam.getPassword()).concat(newUser.getSalt()) ;
        newUser.setPassword(DigestUtils.md5DigestAsHex(code.getBytes()));
        newUser.setName(userRegisterParam.getAccount());
        userService.saveUser(newUser);
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setUserId(userService.getUserByName(userRegisterParam.getAccount()).getId());
        newUserInfo.setName(userRegisterParam.getName());
        newUserInfo.setAddress(userRegisterParam.getAddress());
        userService.saveUserInfo(newUserInfo);
        UserRole newUserRole = new UserRole();
        newUserRole.setUserId(userService.getUserByName(userRegisterParam.getAccount()).getId());
        newUserRole.setRoleId(Long.valueOf(3));
        userService.saveRole(newUserRole);
        return ResponseMsg.successResponse("OK");
    }


}
