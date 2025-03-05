package com.fulan.application;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/6/6 11:22
 */
public class DateTest {

    @Test
    public void dateTest() throws ParseException {
        Date date1 = DateUtils.parseDate("2017-12-13 10:10:10", "yyyy-MM-dd HH:mm:ss");
        Date date2 = DateUtils.parseDate("2017-12-13 12:10:10", "yyyy-MM-dd HH:mm:ss");
        boolean l =
            DateUtils.ceiling(date1, Calendar.DATE).getTime() == DateUtils.ceiling(date2, Calendar.DATE).getTime();

    }

    @Test
    public void getPercent() {

    }

}
