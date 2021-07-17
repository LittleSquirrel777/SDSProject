package com.iman.sds.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iman.sds.entity.Score;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    Score getScoreById2(Long id);



    @Select("select id from user where name = #{factoryName}")
    Long searchIdByName(String factoryName);

    @Select("select * from Score where factory_id = #{id}")
        //@Options(useGeneratedKeys = true,keyProperty = "id")
    List<Score> getScoreById(Long id);

}
