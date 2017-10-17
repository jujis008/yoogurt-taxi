package com.yoogurt.taxi.notification.bo;

import com.yoogurt.taxi.notification.config.IGeTuiConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransmissionPayload {

    /**
     * 个推配置信息
     */
    private IGeTuiConfig config;

    /**
     * 透传信息
     */
    private Transmission transmission;

}
