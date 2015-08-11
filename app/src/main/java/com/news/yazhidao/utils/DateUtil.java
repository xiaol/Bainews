package com.news.yazhidao.utils;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ariesy on 4/27/15.
 */
public class DateUtil {

    //判断是上午还是下午
    public static String getMorningOrAfternoon(long time,TextView tv_time) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int apm = mCalendar.get(Calendar.HOUR_OF_DAY);

        String am = "";
        if (apm > 18 || apm < 6) {
            am = "晚间";
        } else {
            am = "早间";
        }

        tv_time.setText(am);

        return am;
    }

    //获取当前日期 转换为中文形式日期
    public static String getMyDate(String currentDate,TextView tv_month,TextView tv_day) {

        int month = 0;
        int day = 0;
        String currMonth = "";
        String currDay = "";

        month = Integer.parseInt(currentDate.substring(5, 7));
        day = Integer.parseInt(currentDate.substring(8, 10));

        switch (month) {
            case 1:
                currMonth = "01";
                break;

            case 2:
                currMonth = "02";
                break;

            case 3:
                currMonth = "03";
                break;

            case 4:
                currMonth = "04";
                break;

            case 5:
                currMonth = "05";
                break;

            case 6:
                currMonth = "06";
                break;

            case 7:
                currMonth = "07";
                break;

            case 8:
                currMonth = "08";
                break;

            case 9:
                currMonth = "09";
                break;

            case 10:
                currMonth = "10";
                break;

            case 11:
                currMonth = "11";
                break;

            case 12:
                currMonth = "12";
                break;

        }

        switch (day) {

            case 1:
                currDay = "01";
                break;

            case 2:
                currDay = "02";
                break;

            case 3:
                currDay = "03";
                break;

            case 4:
                currDay = "04";
                break;

            case 5:
                currDay = "05";
                break;

            case 6:
                currDay = "06";
                break;

            case 7:
                currDay = "07";
                break;

            case 8:
                currDay = "08";
                break;

            case 9:
                currDay = "09";
                break;

            case 10:
                currDay = "10";
                break;

            case 11:
                currDay = "11";
                break;

            case 12:
                currDay = "12";
                break;

            case 13:
                currDay = "13";
                break;

            case 14:
                currDay = "14";
                break;

            case 15:
                currDay = "15";
                break;

            case 16:
                currDay = "16";
                break;

            case 17:
                currDay = "17";
                break;

            case 18:
                currDay = "18";
                break;

            case 19:
                currDay = "19";
                break;

            case 20:
                currDay = "20";
                break;

            case 21:
                currDay = "21";
                break;

            case 22:
                currDay = "22";
                break;

            case 23:
                currDay = "23";
                break;

            case 24:
                currDay = "24";
                break;

            case 25:
                currDay = "25";
                break;

            case 26:
                currDay = "26";
                break;

            case 27:
                currDay = "27";
                break;

            case 28:
                currDay = "28";
                break;

            case 29:
                currDay = "29";
                break;

            case 30:
                currDay = "30";
                break;

            case 31:
                currDay = "31";
                break;


        }
        tv_month.setText(currMonth);
        tv_day.setText(currDay);
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

    /**
     * 获取当前系统的时间  格式:20150604
     * @return
     */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }
    /**
     * 获取当前系统的时间  格式:2015-06-04 12:12:34
     * @return
     */
    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        return format.format(date);
    }
}
