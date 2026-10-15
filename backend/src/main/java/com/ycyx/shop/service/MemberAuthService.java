package com.ycyx.shop.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycyx.shop.common.constant.RedisKeyConstant;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.JwtUtil;
import com.ycyx.shop.dto.LoginDTO;
import com.ycyx.shop.dto.RegisterDTO;
import com.ycyx.shop.dto.SmsLoginDTO;
import com.ycyx.shop.entity.User;
import com.ycyx.shop.mapper.UserMapper;
import com.ycyx.shop.security.JwtAuthenticationFilter;
import com.ycyx.shop.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 前台会员认证服务：注册、密码登录、验证码登录、短信验证码
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuthService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 发送短信验证码（开发环境：验证码打印日志并随接口返回，5分钟过期）
     */
    public String sendSmsCode(String phone) {
        String code = RandomUtil.randomNumbers(6);
        String key = RedisKeyConstant.SMS_CODE + phone;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofSeconds(RedisKeyConstant.SMS_CODE_EXPIRE));
        log.info("【短信验证码】手机号: {}, 验证码: {}（5分钟内有效）", phone, code);
        return code;
    }

    /**
     * 注册
     */
    public void register(RegisterDTO dto) {
        verifyCode(dto.getPhone(), dto.getCode());
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (count != null && count > 0) {
            throw new BizException(ResultCode.PHONE_ALREADY_REGISTERED);
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StrUtil.isBlank(dto.getNickname()) ? "鲜友" + dto.getPhone().substring(7) : dto.getNickname());
        user.setStatus(1);
        userMapper.insert(user);
    }

    /**
     * 手机号密码登录
     */
    public LoginVO loginByPassword(LoginDTO dto) {
        User user = getUserByPhone(dto.getPhone());
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.USER_PASSWORD_ERROR);
        }
        checkStatus(user);
        return buildLoginVO(user);
    }

    /**
     * 手机号验证码登录
     */
    public LoginVO loginBySms(SmsLoginDTO dto) {
        verifyCode(dto.getPhone(), dto.getCode());
        User user = getUserByPhone(dto.getPhone());
        checkStatus(user);
        return buildLoginVO(user);
    }

    private User getUserByPhone(String phone) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        return user;
    }

    private void checkStatus(User user) {
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BizException(ResultCode.USER_DISABLED);
        }
    }

    private LoginVO buildLoginVO(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(),
                JwtAuthenticationFilter.LOGIN_TYPE_MEMBER, "member");
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAccount(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setLoginType(JwtAuthenticationFilter.LOGIN_TYPE_MEMBER);
        return vo;
    }

    private void verifyCode(String phone, String code) {
        String key = RedisKeyConstant.SMS_CODE + phone;
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(cached) || !cached.equals(code)) {
            throw new BizException(ResultCode.SMS_CODE_ERROR);
        }
        stringRedisTemplate.delete(key);
    }
}