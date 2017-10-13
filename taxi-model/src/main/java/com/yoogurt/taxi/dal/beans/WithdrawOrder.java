package com.yoogurt.taxi.dal.beans;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "withdraw_order")
public class WithdrawOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 提现人
     */
    @Column(name = "user_id")
    private Long userId;

    private String username;

    /**
     * 充值/提现金额
     */
    private BigDecimal amount;

    /**
     * 工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     */
    private Integer status;

    /**
     * 收款账号
     */
    @Column(name = "destination_account")
    private String destinationAccount;

    /**
     * 预留姓名
     */
    @Column(name = "reserved_name")
    private String reservedName;

    /**
     * 开户行
     */
    @Column(name = "bank_of_deposit")
    private String bankOfDeposit;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    private Long creator;

    @Column(name = "gmt_modify")
    private Date gmtModify;

    private Long modifier;

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
     * 获取提现人
     *
     * @return user_id - 提现人
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置提现人
     *
     * @param userId 提现人
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取充值/提现金额
     *
     * @return amount - 充值/提现金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置充值/提现金额
     *
     * @param amount 充值/提现金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     *
     * @return status - 工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     *
     * @param status 工单状态：10-待处理（PENDING），20-转账中（TRANSFERING），30-处理成功（SUCCESS），40-拒绝（REFUSE），50-处理失败（FAIL）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取收款账号
     *
     * @return destination_account - 收款账号
     */
    public String getDestinationAccount() {
        return destinationAccount;
    }

    /**
     * 设置收款账号
     *
     * @param destinationAccount 收款账号
     */
    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    /**
     * 获取预留姓名
     *
     * @return reserved_name - 预留姓名
     */
    public String getReservedName() {
        return reservedName;
    }

    /**
     * 设置预留姓名
     *
     * @param reservedName 预留姓名
     */
    public void setReservedName(String reservedName) {
        this.reservedName = reservedName;
    }

    /**
     * 获取开户行
     *
     * @return bank_of_deposit - 开户行
     */
    public String getBankOfDeposit() {
        return bankOfDeposit;
    }

    /**
     * 设置开户行
     *
     * @param bankOfDeposit 开户行
     */
    public void setBankOfDeposit(String bankOfDeposit) {
        this.bankOfDeposit = bankOfDeposit;
    }

    /**
     * @return is_deleted
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return gmt_create
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * @param gmtCreate
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * @return creator
     */
    public Long getCreator() {
        return creator;
    }

    /**
     * @param creator
     */
    public void setCreator(Long creator) {
        this.creator = creator;
    }

    /**
     * @return gmt_modify
     */
    public Date getGmtModify() {
        return gmtModify;
    }

    /**
     * @param gmtModify
     */
    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    /**
     * @return modifier
     */
    public Long getModifier() {
        return modifier;
    }

    /**
     * @param modifier
     */
    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }
}