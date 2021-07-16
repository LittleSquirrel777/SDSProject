package com.iman.sds.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iman.sds.entity.User;
import com.iman.sds.mapper.UserMapper;
import com.iman.sds.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
