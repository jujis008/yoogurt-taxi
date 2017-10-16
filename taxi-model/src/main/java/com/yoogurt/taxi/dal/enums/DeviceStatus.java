package com.yoogurt.taxi.dal.enums;

/**
 * 设备状态 10-未绑定, 20-已绑定, 30-黑名单
 * @ClassName: ShopStatus 
 * @version V1.0  
 * @date 2016年12月13日 
 * @author liu.weihao
 */
public enum DeviceStatus {

    UNBIND(10, "未绑定"),
    BIND(20, "已绑定"),
    BLACK_LIST(30, "黑名单");

    private int    status;

    private String detail;

    DeviceStatus(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }

    public static DeviceStatus getEnumByStatus(Integer status) {
    	if(status != null){
	        for (DeviceStatus statusEnum : DeviceStatus.values()) {
	            if (status == statusEnum.getStatus()) {
	                return statusEnum;
	            }
	        }
    	}
        return null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
