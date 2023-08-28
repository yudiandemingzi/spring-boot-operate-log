package com.zhuangpo.operate.log.service.impl;


import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.zhuangpo.operate.log.service.OperatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 获取操作人信息
 *
 * @author xub
 * @since 2023/8/2 上午9:30
 */
@Service
public class OperatorServiceImpl implements OperatorService {


    @Override
    public String getOperatorName() {
        //这里模拟从redis或者sisson中获取用户信息
        return "张三";
    }

    public static void main(String[] args) {
        String mealIds = "1a,2";
        Set<Long> mealIdSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(mealIds)) {
            mealIdSet = Splitter.on(",").splitToList(mealIds).stream().map(e -> Long.valueOf(e)).collect(Collectors.toSet());
        }
        System.out.println("mealIdSet = " + mealIdSet);
    }


}
