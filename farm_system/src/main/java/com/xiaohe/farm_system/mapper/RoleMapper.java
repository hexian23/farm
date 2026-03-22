package com.xiaohe.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohe.farm_system.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据角色id查询权限id列表
     *
     * @param roleId 角色id
     * @return 权限id列表
     */
    List<Long> selectRolePermissionIds(@Param("roleId") long roleId);
/**
     * 根据角色id列表删除角色权限关系
     *
     * @param roleIds 角色id列表
     */
    void deleteRolePermissionByRoleIds(@Param("roleIds") List<Long> roleIds);
    /**
     * 批量插入角色权限关系
     *
     * @param roleId      角色id
     * @param permissionIds 权限id列表
     */
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
    /**
     * 根据角色id列表删除角色权限关系
     *
     * @param roleIds 角色id列表
     */
    void deleteUserRolesByRoleIds(@Param("roleIds") List<Long> roleIds);
    /**
     * 根据角色id查询用户id列表
     *
     * @param roleId 角色id
     * @return 用户id列表
     */
    List<Long> selectRoleUserIds(@Param("roleId") long roleId);

    /**
     * 根据角色id和用户id列表删除角色权限关系
     *
     * @param roleId  角色id
     * @param userIds 用户id列表
     */
    void deleteUserRolesByRoleIdAndUserIds(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds);
    /**
     * 判断角色下是否存在用户
     *
     * @param roleId 角色id
     * @return 存在返回1,不存在返回0
     */
    int countUserRoleExists(@Param("roleId") Long roleId, @Param("userId") Long userId);

    void insertUserRole(@Param("roleId") Long roleId, @Param("userId") Long userId);
}

