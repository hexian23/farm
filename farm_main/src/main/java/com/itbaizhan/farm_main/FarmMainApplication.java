package com.itbaizhan.farm_main;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * 智慧农场管理系统主启动类
 * 统一启动所有模块
 */
@SpringBootApplication(scanBasePackages = {
        "com.xiaohe.farm_common",
        "com.xiaohe.farm_system"
})
@MapperScan(basePackages = {"com.xiaohe.farm_system.mapper"})
//启动@PerAuthrize
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class FarmMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmMainApplication.class, args);
    }
    //配置分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}


