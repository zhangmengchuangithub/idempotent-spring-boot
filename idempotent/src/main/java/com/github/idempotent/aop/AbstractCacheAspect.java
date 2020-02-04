package com.github.idempotent.aop;

import com.github.idempotent.util.Entry;
import com.github.idempotent.util.MD5Utils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
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
 * @create 2020-01-20 15:20
 */
public abstract class AbstractCacheAspect {

    abstract void cacheAnnotationPointcut();

    abstract Object invokeResourceWithAnnotation(ProceedingJoinPoint pjp) throws Throwable;

    /**
     * 获取aop方法
     * @param pjp
     * @return
     */
    protected Method getMethod(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return signature.getMethod();
    }

    /**
     * 获取uniqueKey
     * @param params
     * @param fields
     * @return
     * @throws Exception
     */
    protected String getUniqueKey(Object[] params, String[] fields) throws Exception {
        if (params == null || params.length == 0) {
            noParametersHandle();
        }
        String uniqueKey = "";
        if (fields != null && fields.length > 0) {
            List<Entry<Object, Map<String, Method>>> paramList = new ArrayList<>(fields.length);
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
                if (StringUtils.isEmpty(propertyValue)){
                    fieldValueIsEmptyHandle(field);
                }
                uniqueKey = uniqueKey.concat(propertyValue.toString());
            }
        } else {
            for (Object param : params) {
                if (param != null) {
                    uniqueKey = uniqueKey.concat(param.toString());
                }
            }
            if (StringUtils.isEmpty(uniqueKey)){
                parametersAreAllEmptyHandle();
            }
            uniqueKey = MD5Utils.MD5Encode(uniqueKey);
        }
        return uniqueKey;
    }

    /**
     * CacheResult
     */
    protected static class CacheResult{
        private boolean cached;
        private Object result;

        public boolean isCached() {
            return cached;
        }

        public void setCached(boolean cached) {
            this.cached = cached;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }
    }

    /**
     * 没有参数的异常处理
     */
    protected abstract void noParametersHandle();

    /**
     * 指定field值为空的异常处理
     * @param field
     */
    protected abstract void fieldValueIsEmptyHandle(String field);

    /**
     * 全部参数都为空的异常处理
     */
    protected abstract void parametersAreAllEmptyHandle();

}
