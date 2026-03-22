package com.xiaohe.farm_common.exception;


import com.xiaohe.farm_common.result.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor


public class BusException extends RuntimeException{
    // 异常码状态码+错误消息
    private CodeEnum codeEnum;
}
