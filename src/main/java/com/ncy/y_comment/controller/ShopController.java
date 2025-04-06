package com.ncy.y_comment.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.Shop;
import com.ncy.y_comment.service.IShopService;
import com.ncy.y_comment.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private IShopService shopService;

    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        shopService.save(shop);
        return Result.ok(shop.getId());
    }

    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        return shopService.update(shop);
    }

    @GetMapping("/of/type")
    public Result queryShopByType(@RequestParam("typeId") Integer typeId,@RequestParam(value = "current", defaultValue = "1") Integer current,
                                  @RequestParam(value = "x", required = false) Double x,
                                  @RequestParam(value = "y", required = false) Double y) {
        return shopService.queryShopByType(typeId,current,x,y);
    }


    @GetMapping("/of/name")
    public Result queryShopByName(@RequestParam(value = "name",required = false) String name,
                                  @RequestParam(value = "current", defaultValue = "1") Integer current) {
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name),"name",name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

        return Result.ok(page);
    }
}
