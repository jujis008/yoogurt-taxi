package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.finance.task.PayTask;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.CompletableFuture;

public interface PayChannelService {

    /**
     * 执行支付任务（异步）
     *
     * @param payTask 支付任务信息
     * @return
     */
    CompletableFuture<ResponseObj> doTask(PayTask payTask);

    /**
     * 参数签名接口
     *
     * @param parameters   参数组装的SortedMap
     * @param parameterMap 字段对应的请求参数，传入null，或者字段名对应的value为null，则以字段名为准
     * @param signType     加密方式，MD5，RSA，RSA2等
     * @param privateKey   加密的私钥
     * @param charset      编码方式
     * @param skipAttrs    从parameters中跳过的属性
     * @return 签名
     */
    String sign(SortedMap<String, Object> parameters, Map<String, Object> parameterMap, String signType, String privateKey, String charset, String... skipAttrs);

    /**
     * 解析回调请求的参数
     *
     * @param request      回调请求对象
     * @param attributeMap 参数与实体类属性的映射
     * @return 参数键值对
     */
    Map<String, Object> parameterResolve(HttpServletRequest request, Map<String, Object> attributeMap);

    /**
     * 签名验证
     *
     * @param parameterMap   请求体，需要从中获取参数
     * @param signType  签名类型
     * @param charset   编码方式
     * @return 是否通过
     */
    boolean signVerify(Map<String, Object> parameterMap, String signType, String charset);
}