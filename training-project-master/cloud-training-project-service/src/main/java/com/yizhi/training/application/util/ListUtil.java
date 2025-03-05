package com.yizhi.training.application.util;

import org.springframework.beans.BeanUtils;

import java.util.List;

public class ListUtil {

    public static List<?> copyList(List<?> p1, List<Object> p2) {
        if (p1.isEmpty()) {
            return null;
        }
        for (Object p : p1) {
            Object p3 = new Object();
            BeanUtils.copyProperties(p, p3);
            p2.add(p3);
        }
        return p2;
    }
}
