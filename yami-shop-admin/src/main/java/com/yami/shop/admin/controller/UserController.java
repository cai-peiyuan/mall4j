

package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.User;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.QueryUtil;
import com.yami.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.yami.shop.common.response.ServerResponseEntity;
import org.springframework.data.redis.core.query.QueryUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;


/**
 * @author lgh on 2018/10/16.
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('admin:user:page')")
    public ServerResponseEntity<IPage<User>> page(User user, PageParam<User> page) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .like(StrUtil.isNotBlank(user.getNickName()), User::getNickName, user.getNickName())
                .like(StrUtil.isNotBlank(user.getUserMobile()), User::getUserMobile, user.getUserMobile())
                .like(StrUtil.isNotBlank(user.getOpenId()), User::getOpenId, user.getOpenId())
                .like(StrUtil.isNotBlank(user.getSex()), User::getSex, user.getSex())
                .eq(user.getStatus() != null, User::getStatus, user.getStatus())
                .eq(user.getIsStaff() != null, User::getIsStaff, user.getIsStaff());
        //根据参数排序
       // QueryUtil.dynamicOrder(queryWrapper, page.getOrderField(), page.getOrder(), User.class);
        QueryUtil.pageOrder(page);
       //queryWrapper.orderByAsc(User::getUserId);
        IPage<User> userPage = userService.page(page, queryWrapper);
        for (User userResult : userPage.getRecords()) {
            userResult.setNickName(userResult.getNickName() == null ? "" : userResult.getNickName());
        }
        return ServerResponseEntity.success(userPage);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{userId}")
    @PreAuthorize("@pms.hasPermission('admin:user:info')")
    public ServerResponseEntity<User> info(@PathVariable("userId") String userId) {
        User user = userService.getById(userId);
        user.setNickName(user.getNickName() == null ? "" : user.getNickName());
        return ServerResponseEntity.success(user);
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('admin:user:update')")
    public ServerResponseEntity<Void> update(@RequestBody User user) {
        user.setModifyTime(new Date());
        user.setNickName(user.getNickName() == null ? "" : user.getNickName());
        userService.updateById(user);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('admin:user:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody String[] userIds) {
        userService.removeByIds(Arrays.asList(userIds));
        return ServerResponseEntity.success();
    }
}
