package com.ncy.y_comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.RateLimiter;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.VoucherOrder;
import com.ncy.y_comment.mapper.VoucherOrderMapper;
import com.ncy.y_comment.rabbitmq.MQSender;
import com.ncy.y_comment.service.IVoucherOrderService;
import com.ncy.y_comment.utils.RedisIdWorker;
import com.ncy.y_comment.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private MQSender mqSender;

    private RateLimiter rateLimiter = RateLimiter.create(10);

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setResultType(Long.class);
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result seckillVoucher(Long voucherId) {
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return Result.fail("try again");
        }

        Long userId = UserHolder.getUser().getId();
        Long r = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString()
        );

        int result = r.intValue();
        if(result !=0){
            return Result.fail(r == 1? "storage short" : "same order");
        }

        VoucherOrder voucherOrder = new VoucherOrder();
        Long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);

        mqSender.send(JSON.toJSONString(voucherOrder));

        return Result.ok(orderId);
    }
}
