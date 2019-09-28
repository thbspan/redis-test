local values = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'limit', 0, 1)
if values == nil then
    return nil
end
redis.call('ZREM', KEYS[1], values[1])
return values[1]
