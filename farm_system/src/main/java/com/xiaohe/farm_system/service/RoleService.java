package com.xiaohe.farm_system.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    /**
     * 获取角色列表
     * @param page 页码
     * @param size 每页数量
     * @param roleName 角色名称
     * @param status 状态
     * @return
     */

    public IPage<Role> findRolePage(int page, int size, String roleName, String status) {
        Page<Role> pageObj = new Page<>(page, size);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(roleName)) {
            queryWrapper.like("role_name", roleName);
        }

        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByAsc("role_sort");

        return roleMapper.selectPage(pageObj, queryWrapper);
    }
    /**
     * 根据id查询角色
     * @param Id 角色 id
     * @return 角色信息,包含权限列表,如果角色不存在则返回 null
     */
    public Role findById(Long Id){
        // 查询角色基本信息
        Role role = roleMapper.selectById(Id);
        if (role != null) {
            List<Long> permissionIds = roleMapper.selectRolePermissionIds(Id);
            if (permissionIds != null && !permissionIds.isEmpty()){
                List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
                role.setPermissions(permissions);
            }
        }
            return role;


    }
    /**
     * 添加角色
     * @param role 角色信息
     * @return 添加成功返回 true, 添加失败返回 false
     */
    public boolean addRole(Role role){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", role.getRoleName());
        Role existRole = roleMapper.selectOne(queryWrapper);
        if (existRole != null) {
            //角色名称已存在
            throw new BusException(CodeEnum.SYS_ROLE_EXIST);
        }
        role.setCreateTime(LocalDateTime.now());
        role.setStatus("0");
        return roleMapper.insert(role) > 0;
    }
    /**
     * 修改角色
     * @param role 角色信息
     * @return 修改成功返回 true, 修改失败返回 false
     */
    public boolean updateRole(Role role){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", role.getRoleName())
                .eq("role_id", role.getRoleId());
        Role existRole = roleMapper.selectOne(queryWrapper);
        if (existRole == null){
            throw new BusException(CodeEnum.SYS_ROLE_EXIST);
        }
        role.setUpdateTime(LocalDateTime.now());
        return false;
    }
    /**
     * 删除角色
     * @param ids 角色 id列 表
     * @return 删除成功返回 true, 删除失败返回 false
     */
    public boolean deleteRole(List<Long> ids){
        //删除角色权限关联
        roleMapper.deleteRolePermissionByRoleIds(ids);

        //删除用户角色关联
        roleMapper.deleteUserRolesByRoleIds(ids);
        //删除角色
        return roleMapper.deleteBatchIds(ids) > 0;
    }
    /**
     * 获取角色下拉列表
     * @return 正常状态角色列表
     */
    public List<Role> getRoleSelectList(){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "0");//0表示正常
        queryWrapper.orderByDesc("role_sort");
        queryWrapper.select("role_id", "role_name");
        return roleMapper.selectList(queryWrapper);
    }
    /**
     * 修改角色状态
     * @param roleId 角色 id
     * @param status 状态
     * @return 修改成功返回 true, 修改失败返回 false
     */
    public boolean updateStatus(Long roleId, String status){
        Role role = new Role() ;
        role.setRoleId(roleId);
        role.setStatus(status);
        role.setUpdateTime(LocalDateTime.now());
        return roleMapper.updateById(role) > 0;

    }
    /**
     * 查询已分配该角色的用户列表
     * @param roleId 角色 id
     * @param page 当前页
     * @param size 每页显示条数
     * @param userName 用户名称
     * @return 已分配该角色的用户分页数据
     */
    public IPage<User> getAssignedUsers(Long roleId, int page, int size, String userName) {
        Page<User> pageObj = new Page<>(page, size);
        // 查询已分配该角色的用户id
        List<Long> userIds = roleMapper.selectRoleUserIds(roleId);
        if (userIds.isEmpty()) {
            return pageObj ;// 返回空分页数据
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.hasText(userName)){
            queryWrapper.like("user_name", userName)
                    .or()
                    .like("nick_name", userName);
        }
        queryWrapper.eq("status", "0");//只查询正常状态用户
        queryWrapper.orderByAsc("create_time");
        return userMapper.selectPage(pageObj, queryWrapper);
    }
    /**
     * 获取未分配的用户列表
     * @param roleId 角色 id
     * @param page 页码
     * @param size 每页数量
     * @param userName 用户名称
     * @return
     */
    public IPage<User> getUnassignedUsers(Long roleId, int page, int size,String userName) {
        Page<User> pageObj = new Page<>(page, size);
        // 查询已分配该角色的用户id
        List<Long> userIds = roleMapper.selectRoleUserIds(roleId);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!userIds.isEmpty()) {
            queryWrapper.notIn("user_id", userIds);
        }
        if (StringUtils.hasText(userName)){
            queryWrapper.like("user_name", userName)
                    .or()
                    .like("nick_name", userName);
        }
        queryWrapper.eq("status", "0");//只查询正常状态用户
        queryWrapper.orderByAsc("create_time");
        return userMapper.selectPage(pageObj, queryWrapper);
    }


    /**
     * 取消角色分配给用户
     * @param roleId 角色 id
     * @param userId 用户 id
     * @return 取消成功返回 true, 取消失败返回 false
     */
    public boolean cancelRoleFromUsers(Long roleId, List<Long>  userId){
        if (userId == null || userId.isEmpty()){
            return false;
        }
        roleMapper.deleteUserRolesByRoleIdAndUserIds(roleId, userId);
        return false;
    }

    /**
     * 批量给角色分配权限
     * @param roleId 角色 id
     * @param permissionIds 权限 id 列表
     * @return 分配成功返回 true, 分配失败返回 false
     */
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        //先删除角色权限关系
        roleMapper.deleteRolePermissionByRoleIds(List.of(roleId));
        //如果peermissionIds不为空,则添加角色权限关系
        if (permissionIds != null && !permissionIds.isEmpty()){
            roleMapper.insertRolePermission(roleId, permissionIds);

        }
        return true;
    }
    /**
     * 分配权限给用户
     * @param roleId 角色 id
     * @param userIds 用户 id
     * @return 分配成功返回 true, 分配失败返回 false
     */
    public boolean assignRoleToUsers(Long roleId, List<Long> userIds){
        if (userIds == null || userIds.isEmpty()){
            return false;
        }
        for (Long userId : userIds){
            int count = roleMapper.countUserRoleExists(roleId, userId);

        if (count == 0){
            roleMapper.insertUserRole(roleId, userId);
        }
        }
        return true;
    }
    /**
     * 获取角色权限列表
     * @param roleId 角色 id
     * @return
     */
    public List<Permission > getRolePermissions(Long roleId){
        List<Long> permissionIds = roleMapper.selectRolePermissionIds(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()){
            return permissionMapper.selectBatchIds(permissionIds);
        }
        return Arrays.asList();
    }


}
