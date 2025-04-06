local voucherId = ARGV[1]

local userId = ARGV[2]

local stockkey = 'seckill:stock:' .. voucherId

local orderkey = 'seckill:order:' .. voucherId

if(tonumber(redis.call('get',stockkey))<=0)then
    return 1
end

if(redis.call('sismember',orderkey,userId)==1)then
    return 2
end

redis.call('incrby',stockkey,-1)
redis.call('sadd',orderkey,userId)
return 0