package com.github.idempotent.util;

import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangmc
 * @create 2020-01-20 16:36
 */
public class UniqueKeyUtils {

    /**
     * 获取uniqueKey
     * @param params
     * @param fields
     * @return
     * @throws Exception
     */
    public static String getUniqueKey(Object[] params, String[] fields) throws Exception {
        String uniqueKey = "";
        if (params != null && params.length > 0) {
            if (fields != null && fields.length > 0) {
                List<Entry<Object, Map<String, Method>>> paramList = new ArrayList<>();
                for (Object param : params) {
                    if (param != null) {
                        Class<?> clazz = param.getClass();
                        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                        Map<String, Method> methodMap = Stream.of(propertyDescriptors)
                                .collect(Collectors.toMap(PropertyDescriptor::getName, PropertyDescriptor::getReadMethod));
                        Entry<Object, Map<String, Method>> entry = new Entry<>(param, methodMap);
                        paramList.add(entry);
                    }
                }
                for (String field : fields) {
                    Object propertyValue = "";
                    for (Entry<Object, Map<String, Method>> entry : paramList) {
                        Object param = entry.getKey();
                        Map<String, Method> methodMap = entry.getValue();
                        if (methodMap.containsKey(field)) {
                            Method readMethod = methodMap.get(field);
                            propertyValue = readMethod.invoke(param);
                        }
                        if (!StringUtils.isEmpty(propertyValue)) {
                            break;
                        }
                    }
                    uniqueKey = uniqueKey.concat(propertyValue.toString());
                }
            } else {
                for (Object param : params) {
                    if (param != null) {
                        uniqueKey = uniqueKey.concat(param.toString());
                    }
                }
                uniqueKey = MD5Utils.MD5Encode(uniqueKey);
            }
        }
        return uniqueKey;
    }

}
