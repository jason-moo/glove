package com.tf.mapper;

import com.tf.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * Created by jason_moo on 2018/11/16.
 */
//public interface UserMapper extends BaseMapper<User>{
public interface UserMapper{

    User getById(@Param("id") Long id);

}
