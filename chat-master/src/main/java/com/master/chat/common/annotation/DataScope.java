package com.master.chat.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author: Yang
 * @date: 2020/12/30
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    public String deptAlias() default "";

    /**
     * 用户表的别名
     */
    public String userAlias() default "";

    /**
     * 用户字段
     */
    public String userColumn() default "";

}
