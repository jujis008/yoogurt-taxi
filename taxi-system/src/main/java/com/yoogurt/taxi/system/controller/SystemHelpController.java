package com.yoogurt.taxi.system.controller;

import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.ReflectUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.system.config.OssConfig;
import com.yoogurt.taxi.system.config.OssTokenModel;
import com.yoogurt.taxi.system.service.OssAssumeRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("common/system")
public class SystemHelpController extends BaseController{
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private OssAssumeRoleService ossAssumeRoleService;


    @RequestMapping(value = "/oss/token",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getOssTocken() {
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        String roleArn = ossConfig.getRoleArn();

        String roleSessionName = getUserName();
        String policy = null;
        ProtocolType protocolType = ProtocolType.HTTPS;
        /*指定的过期时间，单位为秒。过期时间范围：900 ~ 3600，默认值为3600*/
        Long durationSeconds = 900L;
        AssumeRoleResponse assumeRoleResponse = ossAssumeRoleService.assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, protocolType, durationSeconds);
        if (assumeRoleResponse == null) {
            return ResponseObj.fail(StatusCode.SYS_ERROR,"oss获取token失败");
        }
        OssTokenModel model = new OssTokenModel();
        model.setAccessKeyId(assumeRoleResponse.getCredentials().getAccessKeyId());
        model.setAccessKeySecret(assumeRoleResponse.getCredentials().getAccessKeySecret());
        model.setSecurityToken(assumeRoleResponse.getCredentials().getSecurityToken());
        model.setExpiration(assumeRoleResponse.getCredentials().getExpiration());
        model.setArn(assumeRoleResponse.getAssumedRoleUser().getArn());
        model.setAssumedRoleId(assumeRoleResponse.getAssumedRoleUser().getAssumedRoleId());
        return ResponseObj.success(model);
    }

    @RequestMapping(value = "/i/getSelectShow/className/{className}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getSelectShow(@PathVariable(name = "className") String className) {
        List<Map<String, Object>> enumModels = ReflectUtils.enums(className);
        return ResponseObj.success(enumModels);
    }
}
