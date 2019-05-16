package com.cestco.viewlib.calendarView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/3/1.
 */

public class DateHandle {

    //传入时间
    private long time;
    private List<SignDate> lastMonthDays;
    private List<SignDate> nextMonthDays;


    public DateHandle(long time) {
        this.time = time;

    }

    /**
     * 当前月份
     */
    public int getMonth(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.CHINA);
        String monthStr = dateFormat.format(time);
        int month = Integer.valueOf(monthStr);
        return month;
    }

    /**
     * 当前日期
     */
    public int getDay(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.CHINA);
        String dayStr = dateFormat.format(time);
        int day = Integer.valueOf(dayStr);
        return day;
    }

    /**
     * 获取当前时间的年份
     */
    public int getYear(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String yearStr = dateFormat.format(time);
        int year = Integer.valueOf(yearStr);
        return year;
    }

    /**
     * 是否是闰年
     */
    private boolean isLeapYear(long time) {
        return getYear(time) % 4 == 0 && getYear(time) % 100 != 0 || getYear(time) % 400 == 0;
    }

    /**
     * 获取当前时间月份天数
     *
     * @return
     */
    public int getMonthDays(long time) {
        boolean leapYear = isLeapYear(time);
        switch (getMonth(time)) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;

            case 4:
            case 6:
            case 9:
            case 11:
                return 30;

            case 2:
                if (leapYear) {
                    return 29;
                } else return 28;

        }

        return -1;
    }

    /**
     * 获取当前时间不同月份天数
     *
     * @return
     */
    public int getMonthDays(int month) {
        boolean leapYear = isLeapYear(time);

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;

            case 4:
            case 6:
            case 9:
            case 11:
                return 30;

            case 2:
                if (leapYear) {
                    return 29;
                } else return 28;

        }

        return -1;
    }

    /**
     * 获取输入的日期的星期几
     * w 从1到7 1代表星期天，以此类推
     *
     * @return
     */
    public int getWeekDay(long inputTime) {

        Date date = new Date(inputTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        return w - 1;

    }

    /**
     * 获取当前时间的所属行数，行数从0-5（一个月最多六行）
     *
     * @return
     */
    public int getRowIndex(long time) {
        double row = (getDay(time) - getFirstDays(time)) / 7.0;
        int rowIndex = (int) Math.abs(Math.ceil(row));
        return rowIndex;
    }

    /**
     * 获取每月第一周有几天 ，第一列为星期天
     *
     * @return
     */
    public int getFirstDays(long time) {
        Date monthOfFirstDay = new Date(getYear(time) - 1900, getMonth(time) - 1, 1);
        long firstDayTime = monthOfFirstDay.getTime();
        int weekDay = getWeekDay(firstDayTime);
        switch (weekDay) {
            case 0:
                return 7;
            case 1:
                return 6;
            case 2:
                return 5;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
                return 1;
        }
        return -1;
    }

    /**
     * 获取当月天数位置
     *
     * @return
     */
    public int[][] getData() {
        int monthDays = getMonthDays(time);
        int[][] datas = new int[7][7];
        Date monthOfDay;
        for (int i = 1; i <= monthDays; i++) {
            monthOfDay = new Date(getYear(time) - 1900, getMonth(time) - 1, i);
            long monthOfDayTime = monthOfDay.getTime();
            int weekDay = getWeekDay(monthOfDayTime);
            int rowIndex = getRowIndex(monthOfDayTime);
            datas[weekDay][rowIndex] = i;
        }
        return datas;
    }

    List<SignDate> dates;

    /**
     * 获取当月天数位置
     *
     * @return
     */
    public List<SignDate> getData(Set<Integer> datas) {
        int monthDays = getMonthDays(time);
        dates = new ArrayList<>();
        dates.clear();
        Date monthOfDay;

        for (int i = 1; i <= monthDays; i++) {
            monthOfDay = new Date(getYear(time) - 1900, getMonth(time) - 1, i);
            long monthOfDayTime = monthOfDay.getTime();
            int month = getMonth(monthOfDayTime);
            int year = getYear(monthOfDayTime);
            int day = getDay(monthOfDayTime);
            int onTimeDay = getDay(time);
            int onTimeMonth = getMonth(time);
            int onTimeYear = getYear(time);
            int weekDay = getWeekDay(monthOfDayTime);
            int rowIndex = getRowIndex(monthOfDayTime);
            SignDate signDate = new SignDate();

            if (datas != null)
                for (Integer signTime : datas) {
                    if (i == signTime) {
                        signDate.setDateType(DateType.SIGNED);
                        signDate.setCurrentSign(true);
                        break;

                    } else {
                        signDate.setDateType(DateType.UNSIGNED);

                    }
                }
            else {
                signDate.setDateType(DateType.UNSIGNED);
            }
            if (onTimeDay == day && onTimeMonth == month && onTimeYear == year && !signDate.isCurrentSign()) {
                signDate.setDateType(DateType.WAITING);
            }
            signDate.setColuIndex(weekDay);
            signDate.setRowIndex(rowIndex);
            signDate.setDay(i);
            dates.add(signDate);

            if (i == 1) {
                if (onTimeMonth == 1)
                    getLastMonthDays(weekDay, true);
                else
                    getLastMonthDays(weekDay, false);
            }
            if (i == monthDays) {
                getNextMonthDays(weekDay, rowIndex);
            }
        }
//        if (lastMonthDays != null)
//            for (SignDate signDate : lastMonthDays) {
//                dates.add(signDate);
//            }
        return dates;
    }

    /**
     * 获取下个月初始时间天数
     *
     * @param weekDay
     * @param rowIndex
     */
    private void getNextMonthDays(int weekDay, int rowIndex) {
        nextMonthDays = new ArrayList<>();
        int count = 0;
        if (rowIndex == 4) {
            count = 13 - weekDay;
        } else count = 6 - weekDay;
        Date monthOfDay;
        for (int i = 1; i <= count; i++) {
            monthOfDay = new Date(getYear(time) - 1900, getMonth(time), i);
            long time = monthOfDay.getTime();
            int weekDayIndex = getWeekDay(time);
            SignDate signDate = new SignDate();
            if (i > 6 - weekDay) {
                signDate.setRowIndex(rowIndex + 1);
                signDate.setColuIndex(weekDayIndex);
                signDate.setDay(i);
            } else {
                signDate.setDay(i);
                signDate.setColuIndex(weekDayIndex);
                signDate.setRowIndex(rowIndex);
            }
            signDate.setDateType(DateType.UNSIGNED);
            nextMonthDays.add(signDate);
        }

    }


    /**
     * 获取上个月剩余天数
     *
     * @param weekday 终止条件
     */
    public void getLastMonthDays(int weekday, boolean firstMonth) {
        int month = getMonth(time);
        lastMonthDays = new ArrayList<>();
        int monthDays;
        if (firstMonth)
            monthDays = 31;
        else
            monthDays = getMonthDays(month - 1);
        for (int i = 0; i < weekday; i++) {
            SignDate signDate = new SignDate();
            signDate.setDay(monthDays - i);
            signDate.setColuIndex(weekday - i - 1);
            signDate.setRowIndex(0);
            signDate.setDateType(DateType.UNSIGNED);

            lastMonthDays.add(signDate);
        }

    }

    public List<SignDate> getLastMonthDays() {
        return lastMonthDays;
    }

    public List<SignDate> getNextMonthDays(){
        return nextMonthDays;
    }
}
