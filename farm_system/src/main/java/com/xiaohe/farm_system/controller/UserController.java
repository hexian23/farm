package com.xiaohe.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaohe.farm_common.result.BaseResult;
import com.xiaohe.farm_system.entity.User;
import com.xiaohe.farm_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/user")

public class UserController {

    @Autowired

    private UserService userService;
    /**
     * 根据id查询用户
     * @param id
     * @return 用户信息
     */

    @GetMapping("/getUserById")
    public BaseResult<User> findById(Long id){
        User user = userService.findById(Math.toIntExact(id));
        return BaseResult.ok(user);
    }
    /**
     * 分页查询用户
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param userName 用户名，可选
     * @param status 用户状态，可选
     * @return 用户分页结果
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('/user/list')")
    public BaseResult<IPage<User>> getUserList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "status", required = false) String status) {
        IPage<User> result = userService.findUserPage(pageNum, pageSize, userName, status);
        return BaseResult.ok(result);
    }

    /**
     * 新增用户
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping("/addUser")
        public BaseResult addUser(@RequestBody User user){
            boolean result = userService.addUser(user);

            return BaseResult.ok();
        }
        /**
     * 修改用户
     * @param user 用户信息
     * @return 操作结果
     */
    @PutMapping ("/updateUser")
    public BaseResult updateUser(@RequestBody User user){
        userService.updateUser(user);
        return BaseResult.ok();
    }
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    @PutMapping("/resetPassword")
    public BaseResult resetPassword(Long userId, String newPassword){
        userService.resetPassword(userId,newPassword);
        return BaseResult.ok();
    }

    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @return 操作结果
     */
    @PutMapping("/changeStatus")
    public BaseResult changeStatus(Long userId, String status){
        userService.updateStatus(userId,status);
        return BaseResult.ok();
    }
    /**
     * 删除用户
     * @param ids 用户ID字符串，多个ID用逗号分隔
     * @return 操作结果
     */
    @DeleteMapping("/deleteUser")
    public BaseResult deleteUser(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        userService.deleteUser(idList);
        return BaseResult.ok();
        //
    }
    /**
     * 给用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    @PutMapping("/assignRoles")
    public BaseResult assignRoles(@RequestParam("userId") Long userId,
                                  @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return BaseResult.ok();
    }



}
