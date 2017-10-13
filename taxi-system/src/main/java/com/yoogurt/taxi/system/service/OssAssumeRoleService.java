package com.yoogurt.taxi.system.service;

import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

public interface OssAssumeRoleService {
    AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                  String roleArn, String roleSessionName, String policy,
                                  ProtocolType protocolType, Long durationSeconds);
}
