package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.*;
import java.util.Date;

@Table(name = "car_info")
public class CarInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "driver_id")
    private Long driverId;

    /**
     * 车牌号
     */
    @Column(name = "plate_number")
    private String plateNumber;

    /**
     * 车前照
     */
    @Column(name = "car_picture")
    private String carPicture;

    /**
     * 车型
     */
    @Column(name = "vehicle_type")
    private String vehicleType;

    /**
     * 能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    @Column(name = "energy_type")
    private Integer energyType;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 车辆所属单位
     */
    private String company;

    /**
     * 车辆登记时间
     */
    @Column(name = "vehicle_register_time")
    private Date vehicleRegisterTime;


    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return driver_id
     */
    public Long getDriverId() {
        return driverId;
    }

    /**
     * @param driverId
     */
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    /**
     * 获取车牌号
     *
     * @return plate_number - 车牌号
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * 设置车牌号
     *
     * @param plateNumber 车牌号
     */
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    /**
     * 获取车前照
     *
     * @return car_picture - 车前照
     */
    public String getCarPicture() {
        return carPicture;
    }

    /**
     * 设置车前照
     *
     * @param carPicture 车前照
     */
    public void setCarPicture(String carPicture) {
        this.carPicture = carPicture;
    }

    /**
     * 获取车型
     *
     * @return vehicle_type - 车型
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * 设置车型
     *
     * @param vehicleType 车型
     */
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * 获取能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     *
     * @return energy_type - 能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    public Integer getEnergyType() {
        return energyType;
    }

    /**
     * 设置能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     *
     * @param energyType 能源：10（GASOLINE）-汽油，GAS-天然气，20（POWER）-电能，30（HYBRID）-混合，40（OTHER）-其它
     */
    public void setEnergyType(Integer energyType) {
        this.energyType = energyType;
    }

    /**
     * 获取车架号
     *
     * @return vin - 车架号
     */
    public String getVin() {
        return vin;
    }

    /**
     * 设置车架号
     *
     * @param vin 车架号
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * 获取车辆所属单位
     *
     * @return company - 车辆所属单位
     */
    public String getCompany() {
        return company;
    }

    /**
     * 设置车辆所属单位
     *
     * @param company 车辆所属单位
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * 获取车辆登记时间
     *
     * @return vehicle_register_time - 车辆登记时间
     */
    public Date getVehicleRegisterTime() {
        return vehicleRegisterTime;
    }

    /**
     * 设置车辆登记时间
     *
     * @param vehicleRegisterTime 车辆登记时间
     */
    public void setVehicleRegisterTime(Date vehicleRegisterTime) {
        this.vehicleRegisterTime = vehicleRegisterTime;
    }

}