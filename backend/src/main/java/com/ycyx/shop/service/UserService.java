package com.ycyx.shop.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.PageResult;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.SecurityUtils;
import com.ycyx.shop.dto.UserProfileDTO;
import com.ycyx.shop.entity.User;
import com.ycyx.shop.mapper.UserMapper;
import com.ycyx.shop.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 会员服务：个人信息查询/修改 + 后台用户管理
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 查询当前登录会员信息
     */
    public UserVO getCurrentUser() {
        User user = userMapper.selectById(SecurityUtils.getUserId());
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return toVO(user);
    }

    /**
     * 修改个人资料
     */
    public void updateProfile(UserProfileDTO dto) {
        User user = userMapper.selectById(SecurityUtils.getUserId());
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        userMapper.updateById(user);
    }

    /**
     * 后台：会员用户分页查询
     */
    public PageResult<UserVO> pageUsers(long page, long size, String keyword) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getPhone, keyword).or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(p, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toVO).collect(java.util.stream.Collectors.toList()));
    }

    /**
     * 后台：禁用/启用会员账号
     */
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}