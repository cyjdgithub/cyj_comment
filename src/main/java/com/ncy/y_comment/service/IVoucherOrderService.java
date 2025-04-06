package com.ncy.y_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {
    Result seckillVoucher(Long voucherId);
}
