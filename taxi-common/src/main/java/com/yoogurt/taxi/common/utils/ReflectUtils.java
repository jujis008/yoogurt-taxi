package com.yoogurt.taxi.common.utils;

import com.yoogurt.taxi.common.vo.EnumModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReflectUtils {

    private static final String BASE_PACKAGE = "com.yoogurt.taxi.dal.enums";

    public static List<EnumModel> toSelectShow(String className) {
        String fullClassName = BASE_PACKAGE + "." + className;

        try {
            Class<Enum<?>> enumClass = ((Class<Enum<?>>) Class.forName(fullClassName));

            //1.获取所有的属性
            //2.根据属性获取getter方法
            //3.依次调用getter方法，注入值

            // 无参方法名
            Method method = enumClass.getMethod("values");

            // 无参方法名
            Method getCodeMethod = enumClass.getMethod("getCode");

            // 无参方法名
            Method getNameMethod = enumClass.getMethod("getName");

            // 唤醒静态对象方法
            Enum<?>[] enumValues = (Enum<?>[]) method.invoke(null);

            List<EnumModel> enumModels = new ArrayList<>();

            for (Enum<?> activity : enumValues) {
                EnumModel enumModel = new EnumModel();
                enumModel.setProperty(activity.name());
                // 唤醒已知对象方法
                enumModel.setCode(String.valueOf(getCodeMethod.invoke(activity)));
                enumModel.setName(String.valueOf(getNameMethod.invoke(activity)));
                enumModels.add(enumModel);
            }

            return enumModels;

        } catch (Exception e) {
            log.error(fullClassName + ": 反射加载异常", e);
            return null;
        }

    }

    public static List<Map<String, Object>> enums(String simpleName) {
        List<Map<String, Object>> list = new ArrayList<>();
        String fullClassName = BASE_PACKAGE + "." + simpleName;
        try {

            Class<Enum<?>> clz = (Class<Enum<?>>) Class.forName(fullClassName);
            Method valuesMethod = clz.getMethod("values");
            Enum<?>[] values = (Enum<?>[]) valuesMethod.invoke(null);
            Field[] fields = clz.getDeclaredFields();
            for (Enum<?> value : values) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("property",value.name());
                for (Field field : fields) {
                    if(field.getType().equals(value.getClass()) || "$VALUES".equalsIgnoreCase(field.getName())) {
                        continue;
                    }
                    String fieldName = field.getName();
                    String first = StringUtils.left(fieldName, 1);
                    if (StringUtils.isBlank(first)) {
                        continue;
                    }
                    Method method = clz.getMethod("get" + fieldName.replaceFirst(first, first.toUpperCase()));
                    if (method == null) {
                        continue;
                    }
                    map.put(fieldName, method.invoke(value));
                }
                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        //1.获取所有的属性
        //2.根据属性获取getter方法
        //3.依次调用getter方法，注入值
    }

    public static void main(String[] args) {
        List<Map<String, Object>> enums = enums("UserType");
        toSelectShow("UserType");
    }

    public static Object getProperty(Object obj, String name) {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                Field field;
                try {
                    field = clazz.getDeclaredField(name);
                } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                    continue;
                }
                try {
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (Exception e) {
                    return null;
                } finally {
                    field.setAccessible(false);
                }
            }
        }
        return null;
    }

    public static boolean setProperty(Object obj, String name, Object value) {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                Field field;
                try {
                    field = clazz.getDeclaredField(name);
                } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                    continue;
                }
                try {
                    Class<?> type = field.getType();
                    if (type.isPrimitive() && value != null) {
                        if (value instanceof String) {
                            if (type.equals(int.class)) {
                                value = Integer.parseInt((String) value);
                            } else if (type.equals(double.class)) {
                                value = Double.parseDouble((String) value);
                            } else if (type.equals(boolean.class)) {
                                value = Boolean.parseBoolean((String) value);
                            } else if (type.equals(long.class)) {
                                value = Long.parseLong((String) value);
                            } else if (type.equals(byte.class)) {
                                value = Byte.parseByte((String) value);
                            } else if (type.equals(char.class)) {
                                value = Character.valueOf(((String) value).charAt(0));
                            } else if (type.equals(float.class)) {
                                value = Float.parseFloat((String) value);
                            } else if (type.equals(short.class)) {
                                value = Short.parseShort((String) value);
                            }
                        }
                        field.setAccessible(true);
                        field.set(obj, value);
                        field.setAccessible(false);
                    }
                    if (value == null || type.equals(value.getClass())) {
                        field.setAccessible(true);
                        field.set(obj, value);
                        field.setAccessible(false);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * <p class="detail">
     * 功能：获取当前方法的全路径
     * </p>
     *
     * @return
     * @author wudy
     * @date 2016年11月17日
     */
    public static String getCurrentClassMethodName() {
        return Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}