package com.yoogurt.taxi.dal.aop;

import com.alibaba.fastjson.JSON;
import com.yoogurt.taxi.common.helper.ServletHelper;
import com.yoogurt.taxi.common.helper.TokenHelper;
import com.yoogurt.taxi.dal.annotation.Access;
import com.yoogurt.taxi.dal.beans.AccessRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;


@Slf4j
@Aspect
@Component
public class AccessAspect{
    @Autowired
    private TokenHelper tokenHelper;

    @AfterReturning(value = "@annotation(access)",returning = "rtv")
    public void after(JoinPoint joinPoint, Access access, Object rtv) {
        HttpServletRequest request = ServletHelper.getRequest();
        String username = tokenHelper.getUserName(request);
        Integer userType = tokenHelper.getUserType(request);
        String desc = access.description();
        String tableName = access.tableName();
        String value = access.value();

        if (!UserType.getEnumsByCode(userType).isWebUser()) {
            return;
        }

        log.info(JSON.toJSONString(rtv));

        AccessRecord record = new AccessRecord();
        record.setDescription(desc);
        record.setLinkId(value);
        record.setTableName(tableName);
        record.setUsername(username);
        record.setUserType(userType);
        log.info(JSON.toJSONString(record));
    }

    public static Object getAnnotionObjId(Object object) throws IllegalAccessException {
        Field[] declaredFields = object.getClass().getDeclaredFields();
//        object.getClass().get
        for (Field field:declaredFields) {
            field.setAccessible(true);
            Id annotation = field.getAnnotation(Id.class);
            if (annotation !=null) {
                return field.get(object);
            }
        }
        return null;
    }

    public static void main(String[] args) throws IllegalAccessException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1232131L);
        userInfo.setUsername("吴德友");
        Object annotionObjId = getAnnotionObjId(userInfo);
        System.out.println(JSON.toJSONString(annotionObjId));
    }

}
