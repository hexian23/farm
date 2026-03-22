package com.xiaohe.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaohe.farm_common.result.BaseResult;
import com.xiaohe.farm_system.entity.Permission;
import com.xiaohe.farm_system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台权限
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param permissionName 权限名称，可选
     * @return 权限分页结果
     */
    @GetMapping("/list")
    public BaseResult<IPage<Permission>> getPermissionList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "permissionName", required = false) String permissionName) {
        IPage<Permission> result = permissionService.findPermissionPage(pageNum, pageSize, permissionName);
        return BaseResult.ok(result);
    }

    /**
     * 根据ID查询权限
     * @param id 权限ID
     * @return 权限信息
     */
    @GetMapping("/getPermissionById")
    public BaseResult<Permission> findById(@RequestParam("id") Long id) {
        Permission permission = permissionService.findById(id);
        return BaseResult.ok(permission);
    }

    /**
     * 获取所有权限
     * @return 所有权限列表
     */
    @GetMapping("/all")
    public BaseResult<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return BaseResult.ok(permissions);
    }

    /**
     * 获取权限下拉列表
     * @return 权限列表
     */
    @GetMapping("/selectList")
    public BaseResult<List<Permission>> getPermissionSelectList() {
        List<Permission> permissions = permissionService.getPermissionSelectList();
        return BaseResult.ok(permissions);
    }


    /**
     * 新增权限
     * @param permission 权限信息
     * @return 操作结果
     */
    @PostMapping("/addPermission")
    public BaseResult addPermission(@RequestBody @Validated Permission permission) {
        permissionService.addPermission(permission);
        return BaseResult.ok();
    }

    /**
     * 修改权限
     * @param permission 权限信息
     * @return 操作结果
     */
    @PutMapping("/updatePermission")
    public BaseResult updatePermission(@RequestBody @Validated Permission permission) {
        permissionService.updatePermission(permission);
        return BaseResult.ok();
    }

    /**
     * 删除权限
     * @param ids 权限ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deletePermission")
    public BaseResult deletePermission(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        permissionService.deletePermission(idList);
        return BaseResult.ok();
    }
}
