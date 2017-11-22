package com.yoogurt.taxi.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Description:
 * 日期验证器。
 * 使用此验证器的默许前提条件是 startTime < endTime，
 * 即 不做时间先后顺序判断。
 * @author Eric Lau(weihao.liu@gogen.com.cn)
 * @Date 2017/4/13.
 */
@Setter
@Getter
public class DateTimeSection implements Comparable<DateTimeSection>, Serializable {

    private Date startTime;

    private Date endTime;

    private Long period;

    public DateTimeSection() {
    }

    public DateTimeSection(Date startTime, Date endTime) {

        this.startTime = startTime;
        this.endTime = endTime;
        this.period = Math.abs(endTime.getTime() - startTime.getTime());
    }

    /**
     * 是否包含另一个时间区段
     * @param other 比较的对象
     * @return
     */
    public boolean contains(DateTimeSection other) {
        return Math.max(other.getStartTime().getTime(), other.getEndTime().getTime()) <= Math.max(this.startTime.getTime(), (this.endTime.getTime()));
    }

    /**
     * 判断两个区间是否有重叠
     *
     * @param other 比较的对象
     * @return
     */
    public boolean isInclude(DateTimeSection other) {

        return Math.max(this.startTime.getTime(), this.endTime.getTime()) > Math.min(other.getStartTime().getTime(), other.getEndTime().getTime());
    }

    /**
     * 计算两个时间区间重叠的长度.
     * 特别：
     * 2017-04-10 15:00:00 ~ 16:00:00
     * 2017-04-10 16:00:00 ~ 17:00:00
     * 以上两个时间段，程序上认为重叠了1000ms，精度要求不高的场景下可以忽略。
     * @param other 比较的对象
     * @return 重叠的毫秒数
     */
    public long getOverLength(DateTimeSection other){
        if(!isInclude(other)) {
            return 0;
        }
        long len = Math.max(this.startTime.getTime(), this.endTime.getTime()) - Math.min(other.getStartTime().getTime(), other.getEndTime().getTime());
        return len==0?1000:len;
    }

    /**
     * 判断两个时间区间是否连续。
     * 前一个结束时间是后一个开始时间，即可认为是连续的。
     * @param other
     * @return
     */
    public boolean isContinuous(DateTimeSection other){
        return other.getStartTime().getTime() - this.endTime.getTime() == 0;
    }

    /**
     * 判断两个区间相隔的跨度，如果连续的话，跨度为0
     * 2017-04-10 15:00:00 ~ 16:00:00
     * 2017-04-10 16:30:00 ~ 17:00:00
     * 以上两个区间的跨度为30分钟
     * @param other 比较的对象
     * @return 跨度时间，以毫秒为单位
     */
    public long getRange(DateTimeSection other){
        if(isContinuous(other)) {
            return 0;
        }
        return Math.min(other.getStartTime().getTime(), other.getEndTime().getTime()) - Math.max(this.getStartTime().getTime(), this.getEndTime().getTime());
    }

    /**
     * 静态方法，直接比较两个时间区段是否有重叠
     * @param sectionA 区间A
     * @param sectionB 区间B
     * @return
     */
    public static boolean isInclude(DateTimeSection sectionA, DateTimeSection sectionB) {
        if(sectionA.compareTo(sectionB) > 0) {
            DateTimeSection t = sectionA;
            sectionA = sectionB;
            sectionB = t;
        }
        return sectionA.isInclude(sectionB);
    }
    /**
     * 时间区间的比较，只比较开始时间的先后
     *
     * @param o 比较的对象
     * @return 小于0表示传入的时间段靠前，大于0表示传入的时间段靠后
     * 如果两个时间段的开始时间一样，也会返回1。
     */
    @Override
    public int compareTo(DateTimeSection o) {

        int result = this.getStartTime().compareTo(o.getStartTime());
        //如果开始时间相等，那么就以时长优先
        if(result == 0) {
            return this.period - o.getPeriod() < 0 ? -1 : 1;
        }
        return result;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINESE);
        return "[" + dateFormat.format(this.startTime) + "-" + dateFormat.format(this.endTime) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateTimeSection)) {
            return false;
        }

        DateTimeSection that = (DateTimeSection) o;

        return startTime.equals(that.startTime) && endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        int result = startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}
