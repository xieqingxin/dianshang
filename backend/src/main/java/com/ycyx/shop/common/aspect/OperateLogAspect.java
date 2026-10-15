package com.ycyx.shop.common.aspect;

import cn.hutool.json.JSONUtil;
import com.ycyx.shop.common.annotation.OperateLog;
import com.ycyx.shop.mapper.OperateLogMapper;
import com.ycyx.shop.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志切面：环绕记录操作人、参数、IP、耗时
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

    private final OperateLogMapper operateLogMapper;

    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            try {
                saveLog(joinPoint, operateLog, System.currentTimeMillis() - start);
            } catch (Exception e) {
                // 日志记录失败不影响主流程
                log.error("操作日志记录失败", e);
            }
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, OperateLog operateLog, long costTime) {
        com.ycyx.shop.entity.OperateLog entity = new com.ycyx.shop.entity.OperateLog();
        entity.setOperation(operateLog.value());
        entity.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        entity.setParams(JSONUtil.toJsonStr(filterArgs(joinPoint.getArgs())));
        entity.setIp(getRequestIp());
        entity.setCostTime(costTime);

        // 当前操作人
        String operator = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            operator = ((LoginUser) authentication.getPrincipal()).getUsername();
        }
        entity.setOperator(operator);
        operateLogMapper.insert(entity);
    }

    /**
     * 过滤无法序列化的参数（请求/响应/上传文件对象）
     */
    private List<Object> filterArgs(Object[] args) {
        List<Object> result = new ArrayList<>();
        if (args == null) {
            return result;
        }
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse
                    || arg instanceof MultipartFile) {
                continue;
            }
            result.add(arg);
        }
        return result;
    }

    private String getRequestIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}