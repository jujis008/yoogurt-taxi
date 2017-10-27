package com.yoogurt.taxi.dal.model.ucpaas;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSms implements Serializable {

    private String appId;

    private String templateId;

    private String to;

    private String param;

}
