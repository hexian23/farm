package com.xiaohe.farm_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码枚举类
 */
@Getter
@AllArgsConstructor


public enum CodeEnum {

    //正常
    SUCCESS(200, "操作成功"),
    //系统异常
    SYSTEM_ERROR(500, "系统异常"),
    //业务异常
    TEST_ERROR(601, "业务异常"),
    SYS_USER_EXIST(602, "用户名已存在"),
    SYS_ROLE_EXIST(603, "角色已存在"),

    SYS_PERMISSION_EXIST(604, "角色已存在");
    private Integer code;
    private String message;
}
