package com.ycyx.shop.common.util;

import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类：获取当前登录用户
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new BizException(ResultCode.UNAUTHORIZED);
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }
}