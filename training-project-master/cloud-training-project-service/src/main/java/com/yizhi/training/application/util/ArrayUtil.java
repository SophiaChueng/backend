package com.yizhi.training.application.util;

import java.util.Collection;

/**
 * @Description TODO
 * @ClassName ArrayUtil
 * @Author shengchenglong
 * @Date 2019-04-02 20:12
 * @Version 1.0
 **/
public class ArrayUtil {

    public static String[] forStringArray(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return new String[0];
        }
        String[] strs = new String[collection.size()];
        int index = 0;
        for (Object o : collection) {
            strs[index] = o.toString();
            index++;
        }
        return strs;
    }
}
