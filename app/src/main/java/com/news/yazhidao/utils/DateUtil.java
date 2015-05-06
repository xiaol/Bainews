package com.news.yazhidao.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ariesy on 4/27/15.
 */
public class DateUtil {

    //判断是上午还是下午
    public static String getMorningOrAfternoon(long time) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int apm = mCalendar.get(Calendar.HOUR_OF_DAY);

        String am = "";
        if (apm > 18 || apm < 6) {
            am = "晚间";
        } else {
            am = "早间";
        }
        return am;
    }

    //获取当前日期 转换为中文形式日期
    public static String getMyDate(String currentDate) {

        int month = 0;
        int day = 0;
        String currMonth = "";
        String currDay = "";

        month = Integer.parseInt(currentDate.substring(5, 7));
        day = Integer.parseInt(currentDate.substring(8, 10));

        switch (month) {
            case 1:
                currMonth = "一月";
                break;

            case 2:
                currMonth = "二月";
                break;

            case 3:
                currMonth = "三月";
                break;

            case 4:
                currMonth = "四月";
                break;

            case 5:
                currMonth = "五月";
                break;

            case 6:
                currMonth = "六月";
                break;

            case 7:
                currMonth = "七月";
                break;

            case 8:
                currMonth = "八月";
                break;

            case 9:
                currMonth = "九月";
                break;

            case 10:
                currMonth = "十月";
                break;

            case 11:
                currMonth = "十一月";
                break;

            case 12:
                currMonth = "十二月";
                break;

        }

        switch (day) {

            case 1:
                currDay = "一日";
                break;

            case 2:
                currDay = "二日";
                break;

            case 3:
                currDay = "三日";
                break;

            case 4:
                currDay = "四日";
                break;

            case 5:
                currDay = "五日";
                break;

            case 6:
                currDay = "六日";
                break;

            case 7:
                currDay = "七日";
                break;

            case 8:
                currDay = "八日";
                break;

            case 9:
                currDay = "九日";
                break;

            case 10:
                currDay = "十日";
                break;

            case 11:
                currDay = "十一日";
                break;

            case 12:
                currDay = "十二日";
                break;

            case 13:
                currDay = "十三日";
                break;

            case 14:
                currDay = "十四日";
                break;

            case 15:
                currDay = "十五日";
                break;

            case 16:
                currDay = "十六日";
                break;

            case 17:
                currDay = "十七日";
                break;

            case 18:
                currDay = "十八日";
                break;

            case 19:
                currDay = "十九日";
                break;

            case 20:
                currDay = "二十日";
                break;

            case 21:
                currDay = "二十一日";
                break;

            case 22:
                currDay = "二十二日";
                break;

            case 23:
                currDay = "二十三日";
                break;

            case 24:
                currDay = "二十四日";
                break;

            case 25:
                currDay = "二十五日";
                break;

            case 26:
                currDay = "二十六日";
                break;

            case 27:
                currDay = "二十七日";
                break;

            case 28:
                currDay = "二十八日";
                break;

            case 29:
                currDay = "二十九日";
                break;

            case 30:
                currDay = "三十日";
                break;

            case 31:
                currDay = "三十一日";
                break;


        }

        String aaa = currMonth + currDay;
        return aaa;
    }

    //判断今天是星期几
    public static String dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        String a = "";
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }

        switch (dayForWeek) {
            case 1:
                a = "星期一";
                break;

            case 2:
                a = "星期二";
                break;

            case 3:
                a = "星期三";
                break;

            case 4:
                a = "星期四";
                break;

            case 5:
                a = "星期五";
                break;

            case 6:
                a = "星期六";
                break;

            case 7:
                a = "星期天";
                break;

        }

        return a;
    }

}
