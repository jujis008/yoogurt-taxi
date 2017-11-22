package com.yoogurt.taxi.order.controller.mobile;

import com.google.common.base.Joiner;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.CommonResource;
import com.yoogurt.taxi.order.service.CommonResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/mobile/order")
public class ResourceMobileController extends BaseController {

    @Autowired
    private CommonResourceService resourceService;

    @RequestMapping(value = "/resources/{linkId}/{name}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getResources(@PathVariable(name = "linkId") String linkId, @PathVariable(name = "name") String name) {

        if (StringUtils.isBlank(linkId)) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "请指定对象");
        }
        if (!"order_pick_up_info".equalsIgnoreCase(name) && !"order_accept_info".equalsIgnoreCase(name)) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "参数有误");
        }

        StringBuilder urlBuilder = new StringBuilder();
        List<CommonResource> resources = resourceService.getResources(linkId, name);
        for (int i=0,size=resources.size();i<size;i++) {
            CommonResource resource = resources.get(i);
            urlBuilder.append(resource.getUrl());
            if(i != size - 1) {
                urlBuilder.append(",");
            }
        }
        return ResponseObj.success(urlBuilder.toString());
    }
}
