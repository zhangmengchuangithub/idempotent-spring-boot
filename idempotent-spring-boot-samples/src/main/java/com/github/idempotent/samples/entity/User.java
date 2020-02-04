package com.github.idempotent.samples.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangmc
 * @create 2020-01-19 10:45
 */
@Data
public class User implements Serializable {

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户年龄
     */
    private Integer age;

    /**
     * 用户姓别
     */
    private Byte gender;

}
