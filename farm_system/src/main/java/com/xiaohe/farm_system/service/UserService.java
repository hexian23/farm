package com.xiaohe.farm_system.service;


import ch.qos.logback.core.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaohe.farm_common.exception.BusException;
import com.xiaohe.farm_common.result.CodeEnum;
import com.xiaohe.farm_system.entity.Permission;
import com.xiaohe.farm_system.entity.Role;
import com.xiaohe.farm_system.entity.User;
import com.xiaohe.farm_system.mapper.PermissionMapper;
import com.xiaohe.farm_system.mapper.RoleMapper;
import com.xiaohe.farm_system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.beans.Encoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Service
@Transactional

public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private PasswordEncoder encoder;

    /**
     * 根据id查询用户
     * @param id
     * @return
     */

    public User findById(Integer id) {
        // 查询用户基本 信息
        User user = userMapper.selectById(id);
        // 查询用户角色信息
        if (user != null){
            List<Long> roleIds = userMapper.selectUserRoleIds(user.getUserId());
            if (roleIds != null && !roleIds.isEmpty()){
                // 查询用户角色信息
                List<Role> roles = roleMapper.selectBatchIds(roleIds);
                //为每个角色查询权限信息
                for (Role role : roles){
                    List<Long> permissionIds = roleMapper.selectRolePermissionIds(role.getRoleId());
                    if (permissionIds != null && !permissionIds.isEmpty()){
                        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
                        role.setPermissions(permissions);
                    }
                }
                user.setRoles(roles);
            }
        }
        return user;
    }

    /**
     * 新增用户
     * @param user 用户信息
     * @return 操作结果
     * */
    public boolean addUser (User user){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        User exstistsUser = userMapper.selectOne(queryWrapper);
        if (exstistsUser != null){
            throw new BusException(CodeEnum.SYS_USER_EXIST);
        }

        //设置默认值
        user.setCreateTime(LocalDateTime.now());
        user.setStatus("0");
        if (!StringUtils.hasText(user.getAvatar())){
            user.setPassword("123456");
        }
        //加密密码
        String password = user.getPassword();
        password = encoder.encode(password);
        user.setPassword(password);
            //保存用户
        return userMapper.insert(user) > 0;

    }
    /**
     * 修改用户
     * @param user 用户信息
     * @return 是否成功
     *
     */
    public boolean updateUser(User user){
       // 检查用户是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();//创建查询条件
        queryWrapper.eq("user_name",user.getUserName())//设置查询条件
                .ne("user_id",user.getUserId());//设置排除条件
        User exstistsUser = userMapper.selectOne(queryWrapper);//查询用户
        if (exstistsUser != null){//用户存在
            throw new BusException(CodeEnum.SYS_USER_EXIST);
        }
        //设置更新时间
        user.setUpdateTime(LocalDateTime.now());
        //如果密码为空，则不修改密码
        if (!StringUtils.hasText(user.getPassword())){
            user.setPassword(null);
        }else {
            //加密密码
            String password = user.getPassword();
            password = encoder.encode(password);
            user.setPassword(password);
        }
        return userMapper.updateById(user) > 0;
    }
/**
 * 重置用户密码
 * @param userId 用户ID
 * @param newPassword 新密码
 * @return 操作结果
 */
public boolean resetPassword(Long userId, String newPassword){
    User user = new User();
    user.setUserId(userId);
    user.setPassword(newPassword);//设置新密码
    user.setPwdUpdateDate(LocalDateTime.now());//设置密码更新时间
    user.setUpdateTime(LocalDateTime.now());//设置更新时间
    return userMapper.updateById(user) > 0;

}
/**
 * 修改用户状态
 * @param userId 用户ID
 * @param status 新状态
 * @return 操作结果
 */
public boolean updateStatus(Long userId, String status){
    User user = new User();
    user.setUserId(userId);
    user.setStatus(status);//设置新状态
    user.setUpdateTime(LocalDateTime.now());
    return userMapper.updateById(user) > 0;
}
    /**
     * 删除用户
     * @param ids 用户ID列表
     * @return 操作结果
     */
    public boolean deleteUser(List<Long> ids) {
        // 删除用户角色关联
        userMapper.deleteUserRolesByUserIds(ids);
        // 删除用户
        return userMapper.deleteBatchIds(ids) > 0;
    }
    /**
     * 分页查询用户
     * @param page 当前页
     * @param size 每页大小
     * @param userName 用户名
     * @param status 状态
     * @return 分页结果
     */
    public IPage<User> findUserPage(int page, int size, String userName, String status) {
        Page<User> pageObj = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(userName)) {
            queryWrapper.like("user_name", userName)
                    .or()
                    .like("nick_name", userName);
        }

        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return userMapper.selectPage(pageObj, queryWrapper);
    }
    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户的所有角色
        List<Long> userIds = new ArrayList();
        userIds.add(userId);
        userMapper.deleteUserRolesByUserIds(userIds);
        // 如果roleIds不为空，则插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            userMapper.insertUserRoles(userId, roleIds);
        }
        return true;
    }
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户
     */
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", username);
        User admin = userMapper.selectOne(queryWrapper);
        return admin;



    }
    /**
     * 根据用户名查询所有权限
     * @param username 用户名
     * @return 权限列表
     */
    public List<Permission> findAllPermission(String username) {
        return userMapper.selectUserPermissions(username);
    }


}


