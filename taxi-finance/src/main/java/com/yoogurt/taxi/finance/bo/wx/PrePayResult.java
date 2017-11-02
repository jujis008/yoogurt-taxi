package com.yoogurt.taxi.finance.bo.wx;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class PrePayResult implements Serializable {

	/** 调用接口提交的公众账号ID */
	private String appId;

	/** 调用接口提交的商户号 */
	private String merchantId;

	/** 微信返回的随机字符串 */
	private String nonceStr;

	/** 微信返回的签名值 */
	private String sign;

	/** JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付 */
	private String tradeType;

	/** 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时 */
	private String prepayId;

	/** 时间戳，精确到秒 */
	private long timestamp;

	/** trade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付 */
	private String codeUrl;

	/** SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断 */
	private String returnCode;

	/** 返回信息，如非空，为错误原因 */
	private String returnMsg;

	/** SUCCESS/FAIL 业务结果 */
	private String resultCode;

	/** 错误代码 */
	private String errCode;

	/** 错误返回的信息描述 */
	private String errCodeDes;
}
