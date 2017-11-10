package com.yoogurt.taxi.pay.service;

import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.pay.params.PayParams;

/**
 * Payment对象管理相关接口
 */
public interface PaymentService extends PayTaskService {

    /**
     * 根据id获取支付对象。
     *
     * @param payId 支付对象id
     * @return 支付对象信息
     */
    Payment getPayment(String payId);


    /**
     * 添加Payment对象
     *
     * @param payment Payment对象
     * @return 添加成功后的Payment对象，返回null表示添加不成功
     */
    Payment addPayment(Payment payment);

    /**
     * 更新Payment对象
     *
     * @param payment Payment对象
     * @return 更新成功后的Payment对象，返回null表示添加不成功
     */
    Payment updatePayment(Payment payment);

    /**
     * 根据id删除Payment对象
     *
     * @param payId 支付对象ID
     * @return 是否删除成功
     */
    boolean deletePayment(String payId);

    /**
     * 通过客户端提交的支付表单信息，生成一个Payment对象
     *
     * @param payParams 支付表单信息
     * @return Payment对象
     */
    Payment buildPayment(PayParams payParams);
}
