package com.xiaohe.farm_system.security;

import com.xiaohe.farm_system.entity.Permission;
import com.xiaohe.farm_system.entity.User;
import com.xiaohe.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Override
    public UserDetails
    loadUserByUsername(String username) throws
            UsernameNotFoundException {
        //认证
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        //授权
            List<Permission> permissions = userService.findAllPermission(username);
            List<GrantedAuthority>
                    grantedAuthorities=new ArrayList<>();
            if (permissions != null&& !permissions.isEmpty()&& !permissions.stream()
                    .allMatch(Objects::isNull)){
                for (Permission permission : permissions){
                        grantedAuthorities.add(new SimpleGrantedAuthority(permission.getComponent()));
                }
            }


        //封装为 UserDetails对象
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .disabled(user.getStatus().equals("1"))//如果状态为1，则禁用
                .authorities(grantedAuthorities)
                .build();
        //返回
        return userDetails;
    }

}
