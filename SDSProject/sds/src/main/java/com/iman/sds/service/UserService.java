package com.iman.sds.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iman.sds.entity.User;
import com.iman.sds.entity.UserLoginParam;

import java.util.List;

/**
 * @author Chris
 * @date 2021/7/12 21:45
 * @Email:gem7991@dingtalk.com
 */

public interface UserService extends IService<User>{
    List<String> queryAllPerms(Long userId);
    void authentic(UserLoginParam userEntity);
    User getUserByName(String userName);
    void updatePassword(String username, String newPassword);
}
