package com.ncy.y_comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.ShopType;

public interface IShopTypeService extends IService<ShopType> {

    Result queryList();
}
