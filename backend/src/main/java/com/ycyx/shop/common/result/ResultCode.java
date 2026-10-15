package com.ycyx.shop.common.result;

/**
 * 统一状态码
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    /* 1xxx - 通用参数/校验错误 */
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    /* 2xxx - 用户相关 */
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_PASSWORD_ERROR(2002, "密码错误"),
    USER_DISABLED(2003, "账号已被禁用"),
    PHONE_ALREADY_REGISTERED(2004, "手机号已注册"),
    SMS_CODE_ERROR(2005, "验证码错误或已过期"),

    /* 3xxx - 商品相关 */
    CATEGORY_NOT_EXIST(3001, "分类不存在"),
    PRODUCT_NOT_EXIST(3002, "商品不存在"),
    PRODUCT_OFF_SHELF(3003, "商品已下架"),
    STOCK_NOT_ENOUGH(3004, "库存不足"),

    /* 4xxx - 订单相关 */
    ORDER_NOT_EXIST(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态不允许该操作"),
    ORDER_REPEAT(4003, "请勿重复提交订单"),
    ADDRESS_NOT_EXIST(4004, "收货地址不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}