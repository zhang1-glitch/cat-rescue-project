package com.catrescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catrescue.entity.UserAuthApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAuthApplyMapper extends BaseMapper<UserAuthApply> {

    @Select("SELECT COUNT(*) FROM user_auth_apply WHERE status = 0")
    Integer countPendingAuth();
}