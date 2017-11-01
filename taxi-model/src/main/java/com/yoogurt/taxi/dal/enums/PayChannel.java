package com.yoogurt.taxi.dal.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付渠道（balance-余额支付，alipay-支付宝 APP 支付，upacp-银联支付，wx-微信 APP 支付,wx_pub-微信公众号支付）
 * @version V1.0
 * @author liu.weihao
 */
@Getter
public enum PayChannel {

    BALANCE("balance", "余额支付"),
    ALIPAY("alipay", "支付宝快捷支付"),
    UPACP("upacp", "银联支付"),
    UPACP_WAP("upacp_wap", "银联网页支付"),
    WX_PUB("wx_pub", "微信公众号支付"),
    WX("wx", "微信支付"),
    OFFLINE("Offline", "线下渠道");

    private String name;

    private String detail;

    PayChannel(String name, String detail) {
        this.name = name;
        this.detail = detail;
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
}
