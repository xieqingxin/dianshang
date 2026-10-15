package com.ycyx.shop.vo;

import lombok.Data;

/**
 * 收货地址视图
 */
@Data
public class AddressVO {

    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Integer isDefault;
}