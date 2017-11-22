package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付渠道（balance-余额支付，alipay-支付宝 APP 支付，upacp-银联支付，wx-微信 APP 支付,wx_pub-微信公众号支付）
 *
 * @author liu.weihao
 * @version V1.0
 */
@Getter
public enum PayChannel {
    /**
     *余额支付
     */
    BALANCE("balance", "余额支付", ""),
    /**
     *支付宝快捷支付
     */
    ALIPAY("alipay", "支付宝快捷支付", "alipayService"),
    /**
     *银联支付
     */
    UPACP("upacp", "银联支付", "upacpService"),
    /**
     * 银联网页支付
     */
    UPACP_WAP("upacp_wap", "银联网页支付", "upacpService"),
    /**
     * 微信公众号支付
     */
    WX_PUB("wx_pub", "微信公众号支付", "wxPayService"),
    /**
     *微信支付
     */
    WX("wx", "微信支付", "wxPayService"),
    /**
     *线下渠道
     */
    OFFLINE("Offline", "线下渠道", "");

    private String name;

    private String detail;

    private String serviceName;

    PayChannel(String name, String detail, String serviceName) {
        this.name = name;
        this.detail = detail;
        this.serviceName = serviceName;
    }

    public static PayChannel getChannelByName(String name) {

        if (StringUtils.isNotBlank(name)) {
            for (PayChannel channel : PayChannel.values()) {
                if (StringUtils.equals(name, channel.getName())) {
                    return channel;
                }
            }
        }
        return null;
    }

    public boolean isAppPay() {
        switch (this) {
            case ALIPAY:
            case WX:
            case UPACP:
                return true;
            default:
                return false;
        }
    }

    public boolean isThirdParty() {
        switch (this) {
            case ALIPAY:
            case WX:
            case WX_PUB:
            case UPACP:
            case UPACP_WAP:
                return true;
            default:
                return false;
        }
    }

    public boolean isWx() {
        switch (this) {
            case WX:
            case WX_PUB:
                return true;
            default:
                return false;
        }
    }

    public boolean isBalance() {
        switch (this) {
            case BALANCE:
                return true;
            default:
                return false;
        }
    }

    public boolean isAlipay() {
        switch (this) {
            case ALIPAY:
                return true;
            default:
                return false;
        }
    }

    public boolean isBank() {
        switch (this) {
            case UPACP:
            case UPACP_WAP:
                return true;
            default:
                return false;
        }
    }
}
