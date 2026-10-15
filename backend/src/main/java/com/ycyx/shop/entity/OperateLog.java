package com.ycyx.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operate_log")
public class OperateLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String operator;

    private String operation;

    private String method;

    private String params;

    private String ip;

    private Long costTime;

    private LocalDateTime createTime;
}