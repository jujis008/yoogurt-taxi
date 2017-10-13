package com.yoogurt.taxi.dal.beans;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user_account")
public class UserAccount {
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    @Column(name = "frozen_balance")
    private BigDecimal frozenBalance;

    /**
     * 应收押金
     */
    @Column(name = "receivable_deposit")
    private BigDecimal receivableDeposit;

    /**
     * 已收押金
     */
    @Column(name = "received_deposit")
    private BigDecimal receivedDeposit;

    /**
     * 冻结押金
     */
    @Column(name = "frozen_deposit")
    private BigDecimal frozenDeposit;

    /**
     * 是否删除，1-是，0-否
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "gmt_modify")
    private Date gmtModify;

    private Long modifier;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 创建者
     */
    private Long creator;

    /**
     * @return user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取可用余额
     *
     * @return balance - 可用余额
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * 设置可用余额
     *
     * @param balance 可用余额
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * 获取冻结余额
     *
     * @return frozen_balance - 冻结余额
     */
    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    /**
     * 设置冻结余额
     *
     * @param frozenBalance 冻结余额
     */
    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    /**
     * 获取应收押金
     *
     * @return receivable_deposit - 应收押金
     */
    public BigDecimal getReceivableDeposit() {
        return receivableDeposit;
    }

    /**
     * 设置应收押金
     *
     * @param receivableDeposit 应收押金
     */
    public void setReceivableDeposit(BigDecimal receivableDeposit) {
        this.receivableDeposit = receivableDeposit;
    }

    /**
     * 获取已收押金
     *
     * @return received_deposit - 已收押金
     */
    public BigDecimal getReceivedDeposit() {
        return receivedDeposit;
    }

    /**
     * 设置已收押金
     *
     * @param receivedDeposit 已收押金
     */
    public void setReceivedDeposit(BigDecimal receivedDeposit) {
        this.receivedDeposit = receivedDeposit;
    }

    /**
     * 获取冻结押金
     *
     * @return frozen_deposit - 冻结押金
     */
    public BigDecimal getFrozenDeposit() {
        return frozenDeposit;
    }

    /**
     * 设置冻结押金
     *
     * @param frozenDeposit 冻结押金
     */
    public void setFrozenDeposit(BigDecimal frozenDeposit) {
        this.frozenDeposit = frozenDeposit;
    }

    /**
     * 获取是否删除，1-是，0-否
     *
     * @return is_deleted - 是否删除，1-是，0-否
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除，1-是，0-否
     *
     * @param isDeleted 是否删除，1-是，0-否
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
     * 获取创建者
     *
     * @return creator - 创建者
     */
    public Long getCreator() {
        return creator;
    }

    /**
     * 设置创建者
     *
     * @param creator 创建者
     */
    public void setCreator(Long creator) {
        this.creator = creator;
    }
}