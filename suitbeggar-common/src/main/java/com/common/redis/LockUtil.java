package com.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fangyitao on 2019/8/19.
 * 分布式锁实现
 */
public class LockUtil {
    private static final Logger log = LoggerFactory.getLogger(LockUtil.class);

    public LockUtil() {
        super();
    }

    /**
     * 加锁
     * @param lockName 可以为共享变量名，也可以为方法名，主要是用于模拟锁信息
     * @param time 超时时间
     * @return
     */
    public static boolean lock(String lockName,String time){
        log.info(Thread.currentThread() + "开始尝试加锁！");
        Long result = RedisPoolUtil.setnx(lockName, String.valueOf(System.currentTimeMillis() + time));
        if(result !=null && result.intValue() == 1) {
            log.info(Thread.currentThread() + "加锁成功！");
            RedisPoolUtil.expire(lockName, 5);
            log.info(Thread.currentThread() + "执行业务逻辑！");
            //RedisPoolUtil.del(lockName);
            return true;
        }else {
            String lockValueA = RedisPoolUtil.get(lockName);
            if (lockValueA != null && Long.parseLong(lockValueA) >= System.currentTimeMillis()){
                String lockValueB = RedisPoolUtil.getSet(lockName, String.valueOf(System.currentTimeMillis() + time));
                if (lockValueB == null || lockValueB.equals(lockValueA)){
                    log.info(Thread.currentThread() + "加锁成功！");
                    RedisPoolUtil.expire(lockName, 5);
                    log.info(Thread.currentThread() + "执行业务逻辑！");
                    //RedisPoolUtil.del(lockName);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }


    /**
     * 解锁
     * @param lockName
     * @param time 超时时间
     * @return
     */
    public static boolean unlock(String lockName,String time){
        log.info(Thread.currentThread() + "开始尝试解锁！");
        try {
            String lockValueA = RedisPoolUtil.get(lockName);
            if(lockValueA !=null&&lockValueA.equals(String.valueOf(System.currentTimeMillis() + time))){
                RedisPoolUtil.del(lockName);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread() + "解锁异常！",e);
        }

        log.info(Thread.currentThread() + "解锁完成！");
        return true;
    }

}
