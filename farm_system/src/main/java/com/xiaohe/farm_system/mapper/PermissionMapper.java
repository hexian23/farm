package com.xiaohe.farm_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaohe.farm_system.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据权限ID列表批量删除角色权限关联
     * @param permissionIds 权限ID列表
     */
    void deleteRolePermissionsByPermissionIds(@Param("permissionIds") List<Long> permissionIds);
}
