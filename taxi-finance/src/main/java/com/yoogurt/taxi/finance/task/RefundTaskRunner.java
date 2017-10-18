package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.Refund;

public interface RefundTaskRunner {

	/**
	 * 通过第三方交易平台，获取一个支付对象
	 */
	Refund run(RefundTask task);

}
