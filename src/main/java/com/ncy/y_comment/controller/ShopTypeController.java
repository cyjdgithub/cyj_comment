package com.ncy.y_comment.controller;

import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.service.IShopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Autowired
    private IShopTypeService shopTypeService;

    @RequestMapping("/list")
    public Result queryShopType(){
        return shopTypeService.queryList();
    }

}
