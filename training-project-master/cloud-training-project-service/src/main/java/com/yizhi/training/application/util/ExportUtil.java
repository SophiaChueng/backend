package com.yizhi.training.application.util;

public class ExportUtil {

    /**
     * 替换掉sheet名称中不支持的符号：[ -> 【 ,] -> 】
     *
     * @param sheetName
     * @return
     */
    public static String replaceUnsupportedSheetName(String sheetName) {
        return org.apache.commons.lang3.StringUtils.replaceEach(sheetName,
            new String[] {"[", "]", "/", "\\", ":", "'", "?", "*"}, new String[] {"【", "】", "", "", "：", "", "？", ""});
    }
}
