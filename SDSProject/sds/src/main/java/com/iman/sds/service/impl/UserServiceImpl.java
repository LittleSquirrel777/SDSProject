package com.iman.sds.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iman.sds.common.StatusCode;
import com.iman.sds.common.exception.BizException;
import com.iman.sds.entity.User;
import com.iman.sds.entity.UserInfo;
import com.iman.sds.po.UserLoginParam;
import com.iman.sds.entity.UserRole;
import com.iman.sds.mapper.UserMapper;
import com.iman.sds.service.UserService;
import com.iman.sds.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.List;


/**
 * @author Chris
 * @date 2021/7/12 21:47
 * @Email:gem7991@dingtalk.com
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public void authentic(UserLoginParam userEntity) {
        User user = baseMapper.getUserByName(userEntity.getAccount()) ;

        if(null == user){
            throw new BizException(StatusCode.AUTH_INVALID_CLIENT.getRtCode(), "用户名不存在或联盟中无该用户");
        }

        String code = userEntity.getAccount().concat(userEntity.getPassword()).concat(user.getSalt()) ;
        String md5Password = DigestUtils.md5DigestAsHex(code.getBytes());
        if(!user.getPassword().equals(md5Password)){
            user.setAuthenticated(false);
            throw new BizException(StatusCode.AUTH_INVALID_CLIENT.getRtCode(), "密码错误");
        } else {
            user.setAuthenticated(true);
        }
        CurrentUserService.setUser(user);
    }

    @Override
    public User getUserByName(String userName) {
        return baseMapper.getUserByName(userName);
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        String salt = JwtUtils.generateSalt();
        String code = username.concat(newPassword).concat(salt) ;
        String md5Password = DigestUtils.md5DigestAsHex(code.getBytes());
        baseMapper.updatePassword(username,md5Password,salt);
    }

    public boolean saveUser(User user) {
        return baseMapper.addUser2(user);
    }

    public boolean saveUserInfo(UserInfo userInfo) {
        return baseMapper.addUserInfo2(userInfo);
    }

    public boolean saveRole(UserRole userRole) {
        return userMapper.addUserRole2(userRole);
    };
}
