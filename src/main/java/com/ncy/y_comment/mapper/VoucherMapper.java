package com.ncy.y_comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ncy.y_comment.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);
}
