package com.yoogurt.taxi.dal.model.order;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentListModel extends RentInfoModel {


    /**
     * 出租时长，单位：小时
     */
    private Integer rentTime;

}
