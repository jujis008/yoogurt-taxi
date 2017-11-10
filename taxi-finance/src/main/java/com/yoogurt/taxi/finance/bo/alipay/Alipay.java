package com.yoogurt.taxi.finance.bo.alipay;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Alipay {

    /** 支付宝分配给开发者的应用ID */
    private String appId;

    /** 接口名称：alipay.trade.app.pay */
    private String method = "alipay.trade.app.pay";

    /** 仅支持JSON */
    private String format = "JSON";

    /** 请求使用的编码格式，如utf-8,gbk,gb2312等 */
    private String charset = "utf-8";

    /** 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2 */
    private String signType = "RSA2";

    /** 商户请求参数的签名串 */
    private String sign;

    /** 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss" */
    private String timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");

    /** 调用的接口版本，固定为：1.0 */
    private String version = "1.0";

    /** 支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https */
    private String notifyUrl;

    /** 如果该值为空，则默认为商户签约账号对应的支付宝用户ID */
    private String sellerId;

    /** 业务请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档 */
    private String bizContent;

    /** 商户网站唯一订单号 */
    private String orderNo;

    /**
     * 主题信息，最长为 32 个 Unicode 字符
     */
    private String subject;

    /**
     * 主体信息，最长为 128 个 Unicode 字符
     */
    private String body;

    /** 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY */
    private String productCode = "QUICK_MSECURITY_PAY";

    /**
     * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
     * 1m～15d。m-分钟，h-小时，d-天，1c-当天
     * （1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     * 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
     * 注：若为空，则默认为15d。
     */
    private String timeoutExpress = "5m";

    /** 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000] */
    private BigDecimal totalAmount;

    /**
     * 商品主类型：0—虚拟类商品，1—实物类商品。
     * 注：虚拟类商品不支持使用花呗渠道
     */
    private String goodsType = "0";

    /**
     * 公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。
     * 支付宝会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
     */
    private String passbackParams;


    public Map<String, Object> parameterMap() {
        return new HashMap<String, Object>(){{
            put("appId", "app_id");
            put("signType", "sign_type");
            put("notifyUrl", "notify_url");
            put("bizContent", "biz_content");
            put("orderNo", "out_trade_no");
            put("productCode", "product_code");
            put("sellerId", "seller_id");
            put("totalAmount", "total_amount");
            put("passbackParams", "passback_params");
            put("goodsType", "goods_type");
            put("timeoutExpress", "timeout_express");
        }};
    }
}
