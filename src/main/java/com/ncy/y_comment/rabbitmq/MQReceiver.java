package com.ncy.y_comment.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.ncy.y_comment.config.RabbitMQConfig;
import com.ncy.y_comment.entity.VoucherOrder;
import com.ncy.y_comment.service.ISeckillVoucherService;
import com.ncy.y_comment.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MQReceiver {

    @Autowired
    ISeckillVoucherService seckillVoucherService;

    @Autowired
    IVoucherOrderService voucherOrderService;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String message) {
        log.info("reveive message: {}", message);
        VoucherOrder voucherOrder = JSON.parseObject(message, VoucherOrder.class);
        Long voucherId = voucherOrder.getVoucherId();
        Long userId = voucherOrder.getUserId();
        int count = Math.toIntExact(voucherOrderService.query().eq("user_id", userId).eq("voucher_id", voucherId).count());

        if(count > 0){
            log.error("already bought it");
            return ;
        }

        log.info("used voucherId: {}", voucherId);
        boolean success = seckillVoucherService
                .update()
                .setSql("stock = stock - 1")
                .eq("voucher_id",voucherId)
                .gt("stock",0)
                .update();

        if(!success){
            log.error("update failed");
            return;
        }

        voucherOrderService.save(voucherOrder);

    }

}
