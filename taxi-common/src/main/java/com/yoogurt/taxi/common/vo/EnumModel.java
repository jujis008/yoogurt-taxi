/*
 * 版权所有(C) 浙江天搜科技股份有限公司2016-2020
 * Copyright 2016-2020 Zhejiang Tsou Technology Co., Ltd.
 *  
 * This software is the confidential and proprietary information of
 * Zhejiang Tsou Corporation ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Zhejiang Tsou
 */
package com.yoogurt.taxi.common.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class EnumModel implements Serializable {

    private static final long serialVersionUID = 4208945147011750465L;

    private String            property;

    private String            code;

    private String            name;

}
