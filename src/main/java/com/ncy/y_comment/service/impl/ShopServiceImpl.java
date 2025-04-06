package com.ncy.y_comment.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ncy.y_comment.dto.Result;
import com.ncy.y_comment.entity.Shop;
import com.ncy.y_comment.mapper.ShopMapper;
import com.ncy.y_comment.service.IShopService;
import com.ncy.y_comment.utils.RedisData;
import com.ncy.y_comment.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.ncy.y_comment.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public Result queryById(Long id) {
        //1. penetration
        //Shop shop = querywithPenetration(id);

        //2. breakdown
        Shop shop = queryWithBD(id);
        //3.avalnche
        //Shop shop = queryWithAvalanche(id);

        if(shop == null){
            return Result.fail("did not find shop");
        }

        return Result.ok(shop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(Shop shop) {
        if(shop.getId() == null){
            return Result.fail("id is null");
        }
        updateById(shop);
        stringRedisTemplate.delete(CACHE_SHOP_KEY+shop.getId());
        return Result.ok();
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // need distance to query or not

        // no
        if(x == null || y == null){
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // return data
            return Result.ok(page);
        }

        //yes


        // calculate params
        int from = (current - 1) * SystemConstants.MAX_PAGE_SIZE;
        int end = current * SystemConstants.MAX_PAGE_SIZE;

        String key = SHOP_GEO_KEY + typeId;


        //redis order and pagination

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(key,
                GeoReference.fromCoordinate(x,y),
                new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end));
        if(results == null){
            return Result.ok(Collections.emptyList());
        }

        //get id
        List<GeoResult<GeoLocation<String>>> list = results.getContent();

        if(list.size() < from){
            return Result.ok(Collections.emptyList());
        }

        ArrayList<Long> ids = new ArrayList<>(list.size());
        HashMap<String,Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result ->{
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr,distance);
        });

        String idStr = StrUtil.join(",",ids);

        List<Shop> shops = query().in("id", ids).last("ORDER BY id Field(id,"+ idStr + ")").list();

        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }

        return Result.ok(shops);

    }


    public Shop querywithPenetration(Long id){
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY+id);
        if(StrUtil.isNotBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        if(shopJson != null){
            return null;
        }

        //go to database
        Shop shop = getById(id);

        if(shop == null){
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,null,CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return null;
        }

        String shopJsonStr = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,shopJsonStr,CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return shop;
    }


    public Shop queryWithBD(Long id){
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY+id);
        if(StrUtil.isNotBlank(shopJson)){
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        if(shopJson != null){
            return null;
        }


        Shop shop = null;

        try {
            //mutex lock
            boolean flag = tryLock(LOCK_SHOP_KEY + id);
            while (!flag) {

                Thread.sleep(50);
                return queryWithBD(id);

            }
            shop = getById(id);
            if(shop == null){
                stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,"",CACHE_SHOP_TTL, TimeUnit.MINUTES);
                return null;
            }

            String shopJsonStr = JSONUtil.toJsonStr(shop);
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,shopJsonStr,CACHE_SHOP_TTL, TimeUnit.MINUTES);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }finally {
            unlock(LOCK_SHOP_KEY +id);
        }
        return shop;
    }

    public Shop queryWithAvalanche(Long id){
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY+id);
        if(StrUtil.isBlank(shopJson)){
            return null;
        }

        //deserialize to object
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        //object to shop
        JSONObject jsonObject = JSONUtil.parseObj(redisData.getData());
        Shop shop = JSONUtil.toBean(jsonObject, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        if(LocalDateTime.now().isBefore(expireTime)){
            return shop;
        }

        boolean flag = tryLock(LOCK_SHOP_KEY + id);
        if(!flag){
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    this.saveShop2Redis(id, 20L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    unlock(LOCK_SHOP_KEY + id);
                }
            });
            return shop;
        }
        return shop;
    }



    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key,"1",10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    //release lock
    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }

    public void saveShop2Redis(Long id, Long expireTime) throws InterruptedException {
        Shop shop = getById(id);
        Thread.sleep(200);

        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireTime));
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(redisData));
    }




}
