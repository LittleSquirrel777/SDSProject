package com.iman.sds.controller;

import com.iman.sds.common.ResponseMsg;
import com.iman.sds.common.SalixError;
import com.iman.sds.common.log.SalixLog;
import com.iman.sds.entity.User;
import com.iman.sds.entity.UserInfo;

import com.iman.sds.entity.UserRole;
import com.iman.sds.po.UserLoginParam;
import com.iman.sds.po.UserRegisterParam;
import com.iman.sds.service.UserService;
import com.iman.sds.service.impl.CurrentUserService;
import com.iman.sds.utils.JwtUtils;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    public ResponseMsg login(@RequestBody UserLoginParam userLoginParam) {
        SalixLog salixLog = new SalixLog();
        salixLog.add("account", userLoginParam.getAccount());
        salixLog.add("password", userLoginParam.getPassword());
        logger.info(salixLog.toString());
        if (userLoginParam.getAccount() == null || userLoginParam.getPassword() == null) {
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }

        userService.authentic(userLoginParam);
        if (!CurrentUserService.getUser().isAuthenticated()) {
            return ResponseMsg.errorResponse(SalixError.MSG_USER_VERIFY_ERROR);
        }

        User user = CurrentUserService.getUser();
        String jwtToken = JwtUtils.sign(user.getName(), JwtUtils.SECRET);
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtToken);
        result.put("account",user.getName());
        logger.info(user.getName() +"登录");

        return ResponseMsg.successResponse(result);
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseMsg register(@RequestBody UserRegisterParam userRegisterParam){
        String newUserName = userRegisterParam.getAccount();
        String newUserPassword = userRegisterParam.getPassword();

        if(newUserName == null || userRegisterParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }

        User newUser = new User();
        newUser.setSalt(JwtUtils.generateSalt());
        String code = newUserName.concat(newUserPassword).concat(newUser.getSalt()) ;
        newUser.setPassword(DigestUtils.md5DigestAsHex(code.getBytes()));
        newUser.setName(newUserName);
        userService.saveUser(newUser);
        UserInfo newUserInfo = new UserInfo();
        Long newUserId = (userService.getUserByName(newUserName)).getId();
        newUserInfo.setUserId(newUserId);
        newUserInfo.setName(userRegisterParam.getName());
        newUserInfo.setAddress(userRegisterParam.getAddress());
        userService.saveUserInfo(newUserInfo);
        UserRole newUserRole = new UserRole();
        newUserRole.setUserId(newUserId);
        newUserRole.setRoleId(Long.valueOf(3));
        userService.saveRole(newUserRole);
        return ResponseMsg.successResponse("OK");
    }

    @RequestMapping(value = "/factoryRegister", method = RequestMethod.POST)
    @RequiresPermissions(value = { "fac:add" })
    public ResponseMsg factoryRegister(@RequestBody UserRegisterParam userRegisterParam){
        String newUserName = userRegisterParam.getAccount();
        String newUserPassword = userRegisterParam.getPassword();

        if(newUserName == null || userRegisterParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }


        User newUser = new User();
        newUser.setSalt(JwtUtils.generateSalt());
        String code = newUserName.concat(newUserPassword).concat(newUser.getSalt()) ;
        newUser.setPassword(DigestUtils.md5DigestAsHex(code.getBytes()));
        newUser.setName(newUserName);
        userService.saveUser(newUser);
        UserInfo newUserInfo = new UserInfo();
        Long newUserId = (userService.getUserByName(newUserName)).getId();
        newUserInfo.setUserId(newUserId);
        newUserInfo.setName(userRegisterParam.getName());
        newUserInfo.setAddress(userRegisterParam.getAddress());
        userService.saveUserInfo(newUserInfo);
        UserRole newUserRole = new UserRole();
        newUserRole.setUserId(newUserId);
        newUserRole.setRoleId(Long.valueOf(2));
        userService.saveRole(newUserRole);
        return ResponseMsg.successResponse("OK");
    }

    @RequestMapping(value = "/governmentRegister", method = RequestMethod.POST)
    @RequiresPermissions(value = { "gov:add" })
    public ResponseMsg governmentRegister(@RequestBody UserRegisterParam userRegisterParam){
        String newUserName = userRegisterParam.getAccount();
        String newUserPassword = userRegisterParam.getPassword();

        if(newUserName == null || userRegisterParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }


        User newUser = new User();
        newUser.setSalt(JwtUtils.generateSalt());
        String code = newUserName.concat(newUserPassword).concat(newUser.getSalt()) ;
        newUser.setPassword(DigestUtils.md5DigestAsHex(code.getBytes()));
        newUser.setName(newUserName);
        userService.saveUser(newUser);
        UserInfo newUserInfo = new UserInfo();
        Long newUserId = (userService.getUserByName(newUserName)).getId();
        newUserInfo.setUserId(newUserId);
        newUserInfo.setName(userRegisterParam.getName());
        newUserInfo.setAddress(userRegisterParam.getAddress());
        userService.saveUserInfo(newUserInfo);
        UserRole newUserRole = new UserRole();
        newUserRole.setUserId(newUserId);
        newUserRole.setRoleId(Long.valueOf(1));
        userService.saveRole(newUserRole);
        return ResponseMsg.successResponse("OK");
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseMsg regis(@RequestBody UserLoginParam userLoginParam){
        SalixLog salixLog = new SalixLog();
        salixLog.add("account", userLoginParam.getAccount());
        salixLog.add("password", userLoginParam.getPassword());
        logger.info(salixLog.toString());
        if(userLoginParam.getAccount() == null || userLoginParam.getPassword() == null){
            return ResponseMsg.errorResponse(SalixError.MSG_USER_NAME_PASSWORD_NULL);
        }
        userService.updatePassword(userLoginParam.getAccount(), userLoginParam.getPassword());
        return ResponseMsg.successResponse();
    }
}
