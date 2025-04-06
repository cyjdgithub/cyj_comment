package com.ncy.y_comment.mapper;

import com.ncy.y_comment.entity.User;
import com.ncy.y_comment.entity.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class VoucherMapperTests {

    @Autowired
    private VoucherMapper voucherMapper;

    @Test
    public void selectVoucherById() {
        Long shopId = 1L;
        List<Voucher> list = voucherMapper.queryVoucherOfShop(shopId);
        System.err.println(list);
    }


}
