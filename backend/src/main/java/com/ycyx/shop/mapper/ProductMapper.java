package com.ycyx.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ycyx.shop.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品 Mapper
 */
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 扣减库存（带库存校验，防止超卖，返回受影响行数）
     */
    @Update("UPDATE product SET stock = stock - #{count}, sales = sales + #{count} " +
            "WHERE id = #{id} AND stock >= #{count} AND deleted = 0")
    int deductStock(@Param("id") Long id, @Param("count") Integer count);

    /**
     * 恢复库存（取消订单时调用）
     */
    @Update("UPDATE product SET stock = stock + #{count} WHERE id = #{id} AND deleted = 0")
    int restoreStock(@Param("id") Long id, @Param("count") Integer count);
}