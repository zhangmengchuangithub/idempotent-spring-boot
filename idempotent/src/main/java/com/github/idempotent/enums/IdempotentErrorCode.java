package com.github.idempotent.enums;

/**
 * @author zhangmc
 * @create 2020-01-19 17:30
 */
public enum IdempotentErrorCode {

    /**
     * 幂等方法必须要有一个参数
     */
    MUST_HAVE_AT_LEAST_ONE_PARAMETER,
    /**
     * 幂等方法参数全部为空
     */
    PARAMETERS_ARE_ALL_EMPTY,
    /**
     * 指定成员变量不能为空
     */
    THE_SPECIFIED_FIELD_CANNOT_BE_EMPTY,
    /**
     * 重复提交还未执行成功
     */
    REPEATED_UNSUCCESSFUL_EXECUTION,
    /**
     * 重复提交已执行成功
     */
    REPEATED_EXECUTION_SUCCESSFULLY_PROCESSED,
    ;

}
