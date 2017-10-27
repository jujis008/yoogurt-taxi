package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.finance.dao.PaymentDao;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentServiceImpl extends PayTaskServiceImpl implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * 根据id获取支付对象。
     *
     * @param payId 支付对象id
     * @return 支付对象信息
     */
    @Override
    public Payment getPayment(String payId) {
        if(StringUtils.isBlank(payId)) return null;
        return paymentDao.findOne(payId);
    }

    /**
     * 添加Payment对象
     *
     * @param payment Payment对象
     * @return 添加成功后的Payment对象，返回null表示添加不成功
     */
    @Override
    public Payment addPayment(Payment payment) {
        if(payment == null) return null;
        return paymentDao.insert(payment);
    }

    /**
     * 更新Payment对象
     *
     * @param payment Payment对象
     * @return 更新成功后的Payment对象，返回null表示添加不成功
     */
    @Override
    public Payment updatePayment(Payment payment) {
        if(payment == null) return null;
        return paymentDao.save(payment);
    }

    /**
     * 根据id删除Payment对象
     *
     * @param payId 支付对象ID
     * @return 是否删除成功
     */
    @Override
    public boolean deletePayment(String payId) {
        if(StringUtils.isBlank(payId)) return false;
        try {
            paymentDao.delete(payId);
        } catch (Exception e) {
            log.error("删除支付对象异常:{}", e);
            return false;
        }
        return true;
    }

    /**
     * 通过客户端提交的支付表单信息，生成一个Payment对象
     *
     * @param form 支付表单信息
     * @return Payment对象
     */
    @Override
    public Payment buildPayment(PayForm form) {
        String payId = "pay_" + RandomUtils.getPrimaryKey();
        Payment payment = new Payment(payId);
        BeanUtils.copyProperties(form, payment);
        return payment;
    }
}
