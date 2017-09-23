package com.yoogurt.taxi.dal.model.ucpaas;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class TemplateSms {
    private String appId;
    private String templateId;
    private String to;
    private String param;
}
