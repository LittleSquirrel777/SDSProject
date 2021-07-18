package com.iman.sds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iman.sds.entity.User;
import com.iman.sds.entity.UserInfo;
import com.iman.sds.entity.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;


import java.util.List;

/**
 * @author Chris
 * @date 2021/7/12 21:44
 * @Email:gem7991@dingtalk.com
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{
    User getUserByName(String userName);

    List<String> queryAllPerms(Long userId);

    /**
     * 修改密码
     */
    //void updatePassword(@Param("username") String username, @Param("newPassword") String newPassword, @Param("salt") String salt);
    @Insert("insert into user(`name`,`password`,`salt`) values(#{username},#{md5Password},#{salt})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertData(@Param("username") String username, @Param("md5Password") String newPassword, @Param("salt") String salt);



    void updatePassword(@Param("username") String username, @Param("newPassword") String newPassword,@Param("salt") String salt);

    //@Insert("insert into logistic_info(good_id,description,status,`create_time`) values(#{goodId},#{description},#{status},#{createTime})")
    //@Insert("insert into user values(#{id},#{name},#{password},#{salt})")
    //@Options(useGeneratedKeys = true,keyProperty = "id")
    boolean addUser2(User user);

    boolean addUserInfo2(UserInfo uerInfo);

    //@Insert("insert into user_role values(#{id},#{userId},#{roleId})")
    //@Options(useGeneratedKeys = true,keyProperty = "id")
    boolean addUserRole2(UserRole userRole);
    UserInfo getUserInfoByName2(String name);
}
