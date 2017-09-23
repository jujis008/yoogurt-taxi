package com.yoogurt.taxi.notification.service.Impl;

import com.google.gson.Gson;
import com.yoogurt.taxi.common.utils.DateUtil;
import com.yoogurt.taxi.common.utils.EncryptUtil;
import com.yoogurt.taxi.dal.model.ucpaas.TemplateSms;
import com.yoogurt.taxi.notification.config.SmsConfig;
import com.yoogurt.taxi.notification.service.SmsService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private SmsConfig smsConfig;

    @Override
    public String templateSMS(TemplateSms templateSms) {
        String result = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient client = HttpClients.createDefault();
        String accountSid = smsConfig.getAccountSid();
        String authToken = smsConfig.getAuthToken();
        //MD5加密
        EncryptUtil encryptUtil = new EncryptUtil();
        //构造请求URL内容
        String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
        String signature = getSignature(accountSid, authToken, timestamp, encryptUtil);
        String url = getStringBuffer().append("/").append("2014-06-30").
                append("/Accounts/").append(accountSid).append("/Messages")
                .append("/templateSMS")
                .append("?sig=").append(signature).toString();
        Gson gson = new Gson();
        Map<String,Object> map = new HashMap<>();
        map.put("templateSMS",templateSms);
        String body = gson.toJson(map);
        response = post("application/json", accountSid, timestamp, url, client, encryptUtil, body);
        HttpEntity entity = response.getEntity();
        try {
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public StringBuffer getStringBuffer() {
        StringBuffer sb = new StringBuffer("https://");
        sb.append(smsConfig.getRestServer());
        return sb;
    }

    public String getSignature(String accountSid, String authToken, String timestamp, EncryptUtil encryptUtil) {
        String sig = accountSid + authToken + timestamp;
        String signature = null;
        try {
            signature = encryptUtil.md5Digest(sig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public CloseableHttpResponse post(String cType, String accountSid, String timestamp, String url, CloseableHttpClient httpclient, EncryptUtil encryptUtil, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", cType);
        httpPost.setHeader("Content-Type", cType + ";charset=utf-8");
        String src = accountSid + ":" + timestamp;
        CloseableHttpResponse response = null;
        try {
            String auth = encryptUtil.base64Encoder(src);
            httpPost.setHeader("Authorization", auth);
            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(body.getBytes("UTF-8")));
            requestBody.setContentLength(body.getBytes("UTF-8").length);
            httpPost.setEntity(requestBody);
            // 执行客户端请求
            response = httpclient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
