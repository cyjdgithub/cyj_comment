package com.ncy.y_comment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.SeckillVoucher;
import com.ncy.y_comment.entity.Voucher;
import com.ncy.y_comment.mapper.SeckillVoucherMapper;
import com.ncy.y_comment.mapper.VoucherMapper;
import com.ncy.y_comment.service.ISeckillVoucherService;
import com.ncy.y_comment.service.IVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ncy.y_comment.utils.RedisConstants.SECKILL_STOCK_KEY;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Result queryVoucherOfShop(Long shopId) {
//        return null;
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        save(voucher);
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());

        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(),voucher.getStock().toString());
        seckillVoucherService.save(seckillVoucher);
    }
}
