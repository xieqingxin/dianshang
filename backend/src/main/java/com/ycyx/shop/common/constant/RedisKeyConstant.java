package com.ycyx.shop.common.constant;

/**
 * Redis Key 常量
 */
public class RedisKeyConstant {

    /** 短信验证码 key 前缀 */
    public static final String SMS_CODE = "ycyx:sms:code:";

    /** 购物车 key 前缀 */
    public static final String CART = "ycyx:cart:user:";

    /** 热点商品详情缓存 key 前缀 */
    public static final String HOT_PRODUCT = "ycyx:product:hot:";

    /** 订单幂等 key 前缀 */
    public static final String ORDER_IDEMPOTENT = "ycyx:order:idempotent:";

    /** 短信验证码有效期（秒）: 5分钟 */
    public static final long SMS_CODE_EXPIRE = 5 * 60L;

    /** 热点商品缓存有效期（分钟） */
    public static final long HOT_PRODUCT_EXPIRE = 30L;

    /** 订单幂等有效时长（秒） */
    public static final long ORDER_IDEMPOTENT_EXPIRE = 60 * 60L;

    private RedisKeyConstant() {
    }
}