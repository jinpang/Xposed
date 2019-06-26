package com.jp.allxposed.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 得到昨天的日期
     * @return
     */
    public static String getYestoryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yestoday = sdf.format(calendar.getTime());
        return yestoday;
    }

    /**
     * 得到今天的日期
     * @return
     */
    public static String getTodayDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 时间戳转化为时间格式
     * @param timeStamp
     * @return
     */
    public static String timeStampToStr(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(timeStamp * 1000L);
        return date;
    }

    /**
     * 日期显示
     * @param timeStamp
     * @return
     */
    public static String showDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(timeStamp);
        return date;
    }

    /**
     * 秒转分秒
     * @return
     */
    public static String showMandS(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String date = sdf.format(time);
        return date;
    }


    /**
     * 盆友圈时间显示
     *
     * @param timeStamp
     * @return
     */
    public static String activeShowTime(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000 ;
        long time = curTime - timeStamp;

        if (time < 60 ) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30){
            return  time / 3600 / 24 + "天前";
        }else if(time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12){
            return time / 3600 / 24 / 30 + "个月前";
        }else{
            return time / 3600 / 24 / 30 / 12 + "年前";
        }
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param timeStamp
     * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000 ;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }


    private static SimpleDateFormat sessionTimesdf1 = new SimpleDateFormat("hh");
    private static SimpleDateFormat sessionTimesdf2 = new SimpleDateFormat("mm");
    private static SimpleDateFormat sessionTimesdf3 = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 绘画界面时间标签显示
     * @param timeStamp 显示时间
     * @param nowTime 当前时间
     * @return
     */
    public static String sessionTime(long timeStamp , long nowTime){
        StringBuffer sb = new StringBuffer();

        //1天的秒数
        long oneDaySeccond = 60 * 60 * 24;

        //显示的日子
        int days = (int)((timeStamp + 60 * 60 * 8) / oneDaySeccond);
        //今天的日子
        int nowDays = (int)((nowTime + 60 * 60 * 8) / oneDaySeccond);



        //是同一天
        if(days == nowDays){

        }else if(nowDays - days == 1){
            sb.append("昨天");
        }
        else if(nowDays - days == 2){
            sb.append("前天");
        }
        else {
            if(sessionTimesdf3 == null){
                sessionTimesdf3 = new SimpleDateFormat("yyyy-MM-dd");
            }
            sb.append(sessionTimesdf3.format(timeStamp * 1000));
            sb.append(" ");
        }

        //今天的秒数
        long todaySecconed = (timeStamp + 60 * 60 * 8) % oneDaySeccond;


        if(todaySecconed >= 60 * 60 * 19){
            sb.append("晚上");
        }
        else if(todaySecconed >= 60 * 60 * 13){
            sb.append("下午");
        }
        else if(todaySecconed >= 60 * 60 * 11){
            sb.append("中午");
        }
        else if(todaySecconed >= 60 * 60 * 6){
            sb.append("上午");
        }
        else{
            sb.append("凌晨");
        }

        if(sessionTimesdf1 == null){
            sessionTimesdf1 = new SimpleDateFormat("hh");
        }
        //取消个位小时的0
        sb.append(Integer.parseInt(sessionTimesdf1.format(timeStamp * 1000)) + "");
        sb.append(":");
        if(sessionTimesdf2 == null){
            sessionTimesdf2 = new SimpleDateFormat("mm");
        }
        sb.append(sessionTimesdf2.format(timeStamp * 1000));
        return sb.toString();
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp2(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }

}