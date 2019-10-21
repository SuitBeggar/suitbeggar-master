package com.common.redis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fangyitao on 2019/8/19.
 * 分布式锁实现
 */
public class RedisLock {
    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    public RedisLock() {
        super();
    }

    /**
     *加锁
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value){
        log.info(Thread.currentThread() + "开始尝试加锁！");
        Long result = RedisPoolUtil.setnx(key, value);
        if(result!=null&&result.intValue() == 1){
            log.info(Thread.currentThread() + "加锁成功！");
            return true;
        }
        //假设currentValue=A   接下来并发进来的两个线程的value都是B  其中一个线程拿到锁,除非从始至终所有都是在并发（实际上这中情况是不存在的），只要开始时有数据有先后顺序，则分布式锁就不会出现“多卖”的现象
        String currentValue = RedisPoolUtil.get(key);
        //如果锁过期  解决死锁
        if (!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue) >= System.currentTimeMillis()) {
            //获取上一个锁的时间，锁过期后，GETSET将原来的锁替换成新锁
            String oldValue = RedisPoolUtil.getSet(key, value);
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                log.info(Thread.currentThread() + "加锁成功！");
                return true;
            }
        }

        return false;//拿到锁的就有执行权力，拿不到的只有重新再来，重新再来只得是让用户手动继续抢单
    }

    /**
     *解锁
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = RedisPoolUtil.get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                RedisPoolUtil.del(key);
            }
        }catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }
}
