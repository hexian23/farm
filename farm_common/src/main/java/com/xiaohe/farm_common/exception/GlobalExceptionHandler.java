package com.xiaohe.farm_common.exception;
//统一异常处理器
import com.xiaohe.farm_common.result.BaseResult;
import com.xiaohe.farm_common.result.CodeEnum;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandle(HttpServletRequest req, HttpServletResponse resp, Exception e){
        e.printStackTrace();
        return BaseResult.error(CodeEnum.SYSTEM_ERROR);
    }


    //处理业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult handleBusinessException(BusException e){
        return BaseResult.error(e.getCodeEnum());
    }
//处理权限不足异常，捕获到异常后再次抛出，交给AccessDeniedHandler处理
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandler(AccessDeniedException e) throws AccessDeniedException{
    throw e;
}
}