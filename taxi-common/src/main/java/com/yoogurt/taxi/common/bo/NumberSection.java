/*
 * 版权所有(C) 浙江天搜科技股份有限公司2016-2020
 * Copyright 2016-2020 Zhejiang Tsou Technology Co., Ltd.
 *
 * This software is the confidential and proprietary information of
 * Zhejiang Tsou Corporation ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Zhejiang Tsou
 */
package com.yoogurt.taxi.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 纯整数区间模型类，如[10,200]。
 * 此模型的默许前提条件是待比较的区段集合按照start字段从小到大排序,
 * 因此，使用此区间模型前，请务必使用 {@code Collections.sort(List<NumberSection>)} 进行排序！！！
 * @ClassName: Section
 * @version V1.1
 * @date 2017-4-13
 * @author Eric Lau(weihao.liu@gogen.com.cn)
 */
@Setter
@Getter
public class NumberSection implements Comparable<NumberSection>, Serializable {

	private static final long serialVersionUID = 695679468376567894L;

	private Integer start;	//区间下限

	private Integer end = Integer.MAX_VALUE;	//区段上限，如果想表示1000以上，请不要设置end，或者设置成Integer.MAX_VALUE

	private boolean openLeft = false;	//默认闭区间

	private boolean openRight = true;	//默认开区间

	public NumberSection() {
		super();
	}

	public NumberSection(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}

	public NumberSection(Integer start, Integer end, boolean openLeft, boolean openRight) {
		this.start = start;
		this.end = end;
		this.openLeft = openLeft;
		this.openRight = openRight;
	}

	/**
	 * <p class="detail">
	 * 功能：判断两个区间是否有重叠，可以结合数学的线段重合模型理解。
     * Example：
     * [3, 8) 与 (8, 10)  没有重叠
     * [3, 8] 与 (8, 10)  没有重叠
     * [3, 8] 与 [8, 10)  有重叠
     * [3, 10] 与 (9, 15)  有重叠
	 * </p>
	 * @author Eric Lau(weihao.liu@gogen.com.cn)
	 * @date 2017-4-13
	 * @param other
	 * @return
	 */
	public boolean isInclude(NumberSection other){
        //如果自身的右边开区间，那么需要自身的上限减1
        //如果传入的比较区间左边开区间，那么需要对其下限加1
		return (this.openRight ? (max(this.start, this.end) - 1) : (max(this.start, this.end)))
            >=
            (other.isOpenLeft() ? (min(other.getStart(), other.getEnd()) + 1) : (min(other.getStart(), other.getEnd())));
	}

	/**
	 * <p class="detail">
	 * 功能：计算两个区间重叠的长度。
     * Example：
     * [3, 8) 与 (8, 10)  重叠长度为0
     * [3, 8] 与 (8, 10)  重叠长度为0
     * [3, 8] 与 [8, 10)  重叠长度为1
     * [3, 10] 与 (9, 15) 重叠长度为1
     * [3, 10] 与 [5, 15) 重叠长度为6
     * [3, 10) 与 [8, 10) 重叠长度为2
	 * </p>
	 * @author Eric Lau(weihao.liu@gogen.com.cn)
	 * @date 2017-4-13
	 * @param other
	 * @return
	 */
	public int getOverLength(NumberSection other){
		if(!isInclude(other))	return 0;
		int len = (this.openRight ? (max(this.start, this.end) - 1) : (max(this.start, this.end)))
            -
            (other.isOpenLeft() ? (min(other.getStart(), other.getEnd()) + 1) : (min(other.getStart(), other.getEnd())));
		return len + 1;
	}
	/**
	 * <p class="detail">
	 * 功能：判断两个区间是否连续。
     * Example：
     * [3, 8) 与 (8, 10)  不连续
     * [3, 8] 与 (8, 10)  连续
     * [3, 8] 与 [8, 10)  不连续
     * [3, 10) 与 [8, 10) 不连续
	 * </p>
	 * @author Eric Lau(weihao.liu@gogen.com.cn)
	 * @date 2017-4-13
	 * @param other
	 * @return
	 */
	public boolean isContinuous(NumberSection other){
	    if(getOverLength(other) > 0) return false;  //有重叠长度的两个区间一定不连续
        //区段A的右区间与区段B的左区间相等，并且是一开一闭，不能同开或者同闭。
		return max(this.start, this.end) == min(other.getStart(), other.getEnd()) && !(this.openRight && other.isOpenLeft());
	}
	/**
	 * <p class="detail">
	 * 功能：判断两个区间相隔的跨度，如果连续的话，跨度为0
     * Example：
     * [3, 8) 与 (8, 10)  跨度为0
     * [3, 8] 与 (8, 10)  跨度为1
     * [3, 8] 与 [8, 10)  跨度为0
     * [3, 10) 与 [8, 10) 跨度为-2
     * [3, 10) 与 [15, 20) 跨度为5
	 * </p>
	 * @author Eric Lau(weihao.liu@gogen.com.cn)
	 * @date 2017-4-13
	 * @param other
	 * @param positive  true表示只计算正距离，false表示要考虑负距离
     * @return
	 */
	public int getRange(NumberSection other, boolean positive){
	    //如果只计算正距离，那么就需要考虑两个区间是否连续。如果连续，那么跨度就为0。
		if(positive && isContinuous(other))	return 0;
		return min(other.getStart(), other.getEnd()) - max(this.start, this.end);
	}

	/**
	 * <p class="detail">
	 * 功能：比较两个区间，根据区间下限比较，<strong>从小到大排序。</strong>
	 * </p>
	 * @author Eric Lau(weihao.liu@gogen.com.cn)
	 * @date 2017-4-13
	 * @param other
	 * @return
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(NumberSection other) {
		return this.start - other.getStart();
	}

    /**
     * 格式化输出区间
     * @return
     */
	@Override
	public String toString() {

	    return (this.openLeft ? "(" : "[") + this.start + ", " + this.end + (this.openRight ? ")" : "]");
	}

    /**
     * 取两者最大值
     * @param a
     * @param b
     * @return 最大值
     */
    private int max(int a, int b) {
        return Math.max(a, b);
    }

    /**
     * 取两者最小值
     * @param a
     * @param b
     * @return 最小值
     */
    private int min(int a, int b) {
        return Math.min(a, b);
    }
}
