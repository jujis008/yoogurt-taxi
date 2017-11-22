package com.yoogurt.taxi.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;

/**
 * Description:
 * 时间区段模型类。
 * 使用此类的默许前提条件满足：
 * 1、startTime < endTime，即 不做时间先后顺序判断；
 * 2、左闭右开，即 包含startTime，但不含endTime；
 * 3、this(当前对象)的startTime先于other(传入的比较对象)的startTime。因此，使用此区间模型前，
 * 请务必使用 {@code Collections.sort(List<TimeSection>)} 进行排序！！！
 * @Author Eric Lau(weihao.liu@gogen.com.cn)
 * @Date 2017/4/13.
 */
@Getter
@Setter
public class TimeSection implements Comparable<TimeSection>, Serializable {

    /**
     * 区间开始时间
     */
    private Time startTime;

    /**
     * 区间结束时间
     */
    private Time endTime;

    /**
     * 此区间总共持续的毫秒数
     */
    private long period;

    /**
     * 误差范围，单位：毫秒
     */
    private long scope;

    public TimeSection() {

    }

    public TimeSection(Time startTime, Time endTime) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
        this.period = this.endTime.getTime() - this.startTime.getTime();
    }

    public TimeSection(Time startTime, Time endTime, long scope) {
    	this(startTime, endTime);
		this.scope = scope;
	}

    /**
     * 是否包含另一个时间区段
     * @param other
     * @return
     */
    public boolean contains(TimeSection other) {
        return Math.max(other.getStartTime().getTime(), other.getEndTime().getTime()) <= Math.max(this.startTime.getTime(), (this.endTime.getTime() + this.scope));
    }

    /**
     * 判断两个区间是否有重叠
     * <strong>06:00:00 ~ 09:00:00  与  09:00:00 ~ 12:00:00  无重叠</strong>
     * @param other
     * @return true 有重叠， false 没有重叠
     */
    public boolean isInclude(TimeSection other) {

        return Math.max(this.startTime.getTime(), this.endTime.getTime()) > Math.min(other.getStartTime().getTime(), other.getEndTime().getTime());
    }

    /**
     * 计算两个时间区间重叠的长度.
     * @param other
     * @return 重叠的毫秒数
     */
    public long getOverLength(TimeSection other){
        //如果两个区间不重叠，即可直接返回0
        if(!isInclude(other)) {
            return 0;
        }
        //如果包含，直接返回被包含的时间段持续的毫秒数
        if(contains(other)) {
            return other.getPeriod();
        }
        long len = Math.max(this.startTime.getTime(), this.endTime.getTime()) - Math.min(other.getStartTime().getTime(), other.getEndTime().getTime());
        return len;
    }

    /**
     * 判断两个时间区间是否连续。
     * 前一个结束时间是后一个开始时间，即可认为是连续的。
     * @param other
     * @return
     */
    public boolean isContinuous(TimeSection other){
        return other.getStartTime().getTime() == this.endTime.getTime();
    }

    /**
     * 判断两个区间相隔的跨度，如果两个区间连续或者有重叠，则认为是0跨度
     * 2017-04-10 15:00:00 ~ 16:00:00
     * 2017-04-10 16:30:00 ~ 17:00:00
     * 以上两个区间的跨度为30分钟
     * @param other
     * @return 跨度时间，以毫秒为单位，永远是正距离
     */
    public long getRange(TimeSection other){
        //两个区间连续或者有重叠，则认为是0跨度
        if(isContinuous(other) || isInclude(other)) {
            return 0;
        }
        return Math.min(other.getStartTime().getTime(), other.getEndTime().getTime()) - Math.max(this.getStartTime().getTime(), this.getEndTime().getTime());
    }

    /**
     * 时间区间的比较，只比较开始时间的先后
     * @param o
     * @return -1表示传入的时间段靠前，1表示传入的时间段靠后。
     * 如果两个时间段的开始时间一样，也会返回1。
     */
    @Override
    public int compareTo(TimeSection o) {

        int result = this.getStartTime().compareTo(o.getStartTime());
        //如果开始时间相等，那么就以时长优先
        if(result == 0) {
            return this.period - o.getPeriod() < 0 ? 1 : -1;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeSection section = (TimeSection) o;

        if (!startTime.equals(section.startTime)) {
            return false;
        }
        return endTime.equals(section.endTime);
    }

    @Override
    public int hashCode() {
        int result = startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + this.startTime.toString() + "," + endTime.toString() + ")";
    }
}
