package com.xiaohe.farm_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// security配置类
@Configuration
public class SecurityConfig {
    // Spring Security配置
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 自定义表单登录
        http.formLogin(
                form -> {
                    form.usernameParameter("username") // 用户名项
                            .passwordParameter("password") // 密码项
                            .loginProcessingUrl("/user/login") // 登录提交路径
                            .successHandler(new MyLoginSuccessHandler()) // 登录成功处理器
                            .failureHandler(new MyLoginFailureHandler()); // 登录失败处理器
                }
        );

        // 权限拦截配置
        http.authorizeHttpRequests(
                resp -> {
                    resp.requestMatchers("/user/login","/captcha/generate").permitAll(); // 登录相关请求不需要认证
                    resp.anyRequest().authenticated();// 其余请求都需要认证
                }
        );

        // 退出登录配置
        http.logout(
                logout -> {
                    logout.logoutUrl("/user/logout") // 注销的路径
                            .logoutSuccessHandler(new MyLogoutSuccessHandler()) // 登出成功处理器
                            .clearAuthentication(true) // 清除认证数据
                            .invalidateHttpSession(true); // 清除session
                }
        );

        // 异常处理
        http.exceptionHandling(
                exception -> {
                    exception.authenticationEntryPoint(new MyAuthenticationEntryPoint())// 未登录处理器
                            .accessDeniedHandler(new MyAccessDeniedHandler()); // 权限不足处理器
                }
        );

        // 关闭csrf防护
        http.csrf(csrf ->csrf.disable());

        // 跨域访问
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // 跨域配置对象
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 允许所有来源
        corsConfiguration.addAllowedHeader("*"); // 允许所有请求头
        corsConfiguration.addAllowedMethod("*"); // 允许所有请求方法
        corsConfiguration.setAllowCredentials(false); // 不允许cookie跨域

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 应用于所有路径
        return source;
    }
}
