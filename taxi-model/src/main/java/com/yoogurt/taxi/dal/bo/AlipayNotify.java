package com.yoogurt.taxi.dal.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class AlipayNotify extends Notify {

	/**
	 * 支付宝分配给开发者的应用Id
	 */
	private String appId;

	/**
	 * 通知校验ID
	 */
	private String notifyId;

	/**
	 * 通知的类型：trade_status_sync
	 */
	private String notifyType;

	/**
	 * 调用的接口版本，固定为：1.0
	 */
	private String version;

	/**
	 * 商户业务ID，主要是退款通知中返回退款申请的流水号
	 */
	private String outBizNo;

	/**
	 * 买家支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字
	 */
	private String buyerId;

	/**
	 * 买家支付宝账号
	 */
	private String buyerLogonId;

	/**
	 * 卖家支付宝用户号
	 */
	private String sellerId;

	/**
	 * 卖家支付宝账号
	 */
	private String sellerEmail;

	/**
	 * 交易目前所处的状态：
	 * WAIT_BUYER_PAY	交易创建，等待买家付款
	 * TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
	 * TRADE_SUCCESS	交易支付成功
	 * TRADE_FINISHED	交易结束，不可退款
	 */
	private String tradeStatus;

	/**
	 * 商家在交易中实际收到的款项，单位为元
	 */
	private BigDecimal receiptAmount;

	/**
	 * 用户在交易中支付的可开发票的金额
	 */
	private BigDecimal invoiceAmount;

	/**
	 * 用户在交易中支付的金额
	 */
	private BigDecimal buyerPayAmount;

	/**
	 * 使用集分宝支付的金额
	 */
	private BigDecimal pointAmount;

	/**
	 * 退款通知中，返回总退款金额，单位为元，支持两位小数
	 */
	private BigDecimal refundFee;

	/**
	 * 商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来
	 */
	private String subject;

	/**
	 * 该订单的备注、描述、明细等。对应请求时的body参数，原样通知回来
	 */
	private String body;

	/**
	 * 该笔交易创建的时间。格式为yyyy-MM-dd HH:mm:ss
	 */
	private String gmtCreate;

	/**
	 * 该笔交易的买家付款时间。格式为yyyy-MM-dd HH:mm:ss
	 */
	private String gmtPayment;

	/**
	 * 该笔交易的退款时间。格式为yyyy-MM-dd HH:mm:ss.S
	 */
	private String gmtRefund;

	/**
	 * 该笔交易结束时间。格式为yyyy-MM-dd HH:mm:ss
	 */
	private String gmtClose;

	/**
	 * 支付成功的各个渠道金额信息
	 */
	private String fundBillList;

	/**
	 * 本交易支付时所使用的所有优惠券信息
	 */
	private String voucherDetailList;

	@Override
	public Map<String, Object> attributeMap() {
		return new HashMap<String, Object>(){{
			put("app_id", "appId");
			put("out_trade_no", "orderNo");
			put("sign_type", "signType");
			put("trade_no", "transactionNo");

			put("notify_type", "notifyType");
			put("notify_id", "notifyId");
			put("out_biz_no", "outBizNo");
			put("buyer_id", "buyerId");
			put("buyer_logon_id", "buyerLogonId");
			put("sellerId", "sellerId");
			put("seller_email", "sellerEmail");
			put("trade_status", "tradeStatus");
			put("receipt_amount", "receiptAmount");
			put("invoice_amount", "invoiceAmount");
			put("buyer_pay_amount", "buyerPayAmount");
			put("point_amount", "pointAmount");
			put("refund_fee", "refundFee");
			put("gmt_create", "gmtCreate");
			put("gmt_payment", "gmtPayment");
			put("gmt_refund", "gmtRefund");
			put("gmt_close", "gmtClose");
			put("fund_bill_list", "fundBillList");
			put("voucher_detail_list", "voucherDetailList");
		}};
	}
}
