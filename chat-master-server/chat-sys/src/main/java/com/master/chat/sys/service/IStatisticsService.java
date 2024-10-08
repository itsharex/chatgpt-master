package com.master.chat.sys.service;

import com.master.chat.client.model.dto.Query;

import java.util.List;
import java.util.Map;

/**
 * 统计接口
 *
 * @author: Yang
 * @date: 2023/1/13
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
public interface IStatisticsService {

    /**
     * 获取首页总数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getTotalData(Query query);

    /**
     * 获取折线图数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getLineData(Query query);

    /**
     * 获取雷达图数据
     *
     * @param query 查询条件
     * @return
     */
    List<List<Object>> getRaddarData(Query query);

    /**
     * 获取饼图数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getPieData(Query query);

    /**
     * 获取柱状图总数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getBarData(Query query);

}
