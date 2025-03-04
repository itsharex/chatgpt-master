package com.master.chat.gpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.master.chat.gpt.pojo.entity.User;
import com.master.chat.gpt.pojo.vo.UserVO;
import com.master.chat.client.model.dto.Query;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员用户 Mapper 接口
 *
 * @author: Yang
 * @date: 2023-04-28
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询会员用户列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return
     */
    IPage<UserVO> pageUser(IPage page, @Param("q") Query query);

    /**
     * 查询会员用户列表
     *
     * @param query 查询条件
     * @return
     */
    List<UserVO> listUser(@Param("q") Query query);

    /**
     * 查询会员用户
     *
     * @param query 查询条件
     * @return
     */
    UserVO getUser(@Param("q") Query query);

    /**
     * 根据用户uid获取用户信息
     *
     * @param uid
     * @return
     */
    @Select("select * from gpt_user where uid = #{uid}")
    User getUserByUid(@Param("uid") String uid);

    /**
     * 根据聊天id获取用户信息
     *
     * @param chatId
     * @return
     */
    User getUserByChatId(@Param("chatId") Long chatId);

}
