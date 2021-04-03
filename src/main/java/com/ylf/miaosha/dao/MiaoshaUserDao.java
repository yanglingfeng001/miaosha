package com.ylf.miaosha.dao;

import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao {

    //通过ID获取USer
    @Select("select * from miaosha_user where id=#{id}")
    public MiaoshaUser getById(@Param("id") long id);

    //更新USer
    @Update("update miaosha_user set password=#{password} where id=#{id}")
    public void update(MiaoshaUser toBeUpdate);
}
