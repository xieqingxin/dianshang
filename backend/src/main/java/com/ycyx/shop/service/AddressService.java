package com.ycyx.shop.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ycyx.shop.common.exception.BizException;
import com.ycyx.shop.common.result.ResultCode;
import com.ycyx.shop.common.util.SecurityUtils;
import com.ycyx.shop.dto.AddressDTO;
import com.ycyx.shop.entity.Address;
import com.ycyx.shop.mapper.AddressMapper;
import com.ycyx.shop.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址服务
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    public List<AddressVO> list() {
        List<Address> list = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, SecurityUtils.getUserId())
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(AddressDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Address address = toEntity(dto);
        address.setUserId(userId);

        long count = addressMapper.selectCount(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        if (count == 0) {
            address.setIsDefault(1);
        } else if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(userId);
        }
        addressMapper.insert(address);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(AddressDTO dto) {
        Address address = addressMapper.selectById(dto.getId());
        checkOwner(address);
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        BeanUtil.copyProperties(dto, address, "id", "userId");
        addressMapper.updateById(address);
    }

    public void delete(Long id) {
        Address address = addressMapper.selectById(id);
        checkOwner(address);
        addressMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        Address address = addressMapper.selectById(id);
        checkOwner(address);
        clearDefault(address.getUserId());
        address.setIsDefault(1);
        addressMapper.updateById(address);
    }

    private void clearDefault(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0));
    }

    private void checkOwner(Address address) {
        if (address == null) {
            throw new BizException(ResultCode.ADDRESS_NOT_EXIST);
        }
        if (!address.getUserId().equals(SecurityUtils.getUserId())) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
    }

    private Address toEntity(AddressDTO dto) {
        Address address = new Address();
        BeanUtil.copyProperties(dto, address);
        return address;
    }

    private AddressVO toVO(Address address) {
        AddressVO vo = new AddressVO();
        BeanUtil.copyProperties(address, vo);
        return vo;
    }
}