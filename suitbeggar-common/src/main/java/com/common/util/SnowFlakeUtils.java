package com.common.util;

import java.util.Date;

/**
 * Created by fangyitao on 2019/8/16.
 * SnowFlake算法生成全局唯一ID（用于分布式）
 */
public class SnowFlakeUtils {
    // 起始的时间戳
    private final static long START_STMP = 1480166465631L;
    // 每一部分占用的位数，就三个
    private final static long SEQUENCE_BIT = 12;// 序列号占用的位数
    private final static long MACHINE_BIT = 5; // 机器标识占用的位数
    private final static long DATACENTER_BIT = 5;// 数据中心占用的位数

    /** 支持的最大数据标识id，结果是31 */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /** 机器ID向左移12位 */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    /** 数据标识id向左移17位(12+5) */
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    /** 时间截向左移22位(5+5+12) */
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId; // 数据中心
    private long machineId; // 机器标识
    private long sequence = 0L; // 序列号
    private long lastStmp = -1L;// 上一次时间戳

    public SnowFlakeUtils(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }
    //产生下一个ID
    public synchronized long nextId(){
        long currStmp = getNewstmp();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过  这个时候应当抛出异常
        if (currStmp < lastStmp) {
            throw new RuntimeException("时钟回拨,拒绝生成ID");
            //throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //if条件里表示当前调用和上一次调用落在了相同毫秒内，只能通过第三部分，序列号自增来判断为唯一，所以+1.
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大，只能等待下一个毫秒
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            //不同毫秒内，序列号置为0
            //执行到这个分支的前提是currTimestamp > lastTimestamp，说明本次调用跟上次调用对比，已经不再同一个毫秒内了，这个时候序号可以重新回置0了。
            sequence = 0L;
        }

        lastStmp = currStmp;
        //就是用相对毫秒数、机器ID和自增序号拼接
        //移位  并通过  或运算拼到一起组成64位的ID
        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT      //数据中心部分
                | machineId << MACHINE_LEFT            //机器标识部分
                | sequence;                            //序列号部分
    }

    private long getNextMill(){
        long mill = getNewstmp();

        //本次最新时间小于上次时间，直到下一毫秒。
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    //当前时间
    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        // 构造方法设置机器码：第9个机房的第20台机器
        SnowFlakeUtils snowFlake = new SnowFlakeUtils(9, 20);
        //循环生成2^12个ID
        for (int i = 0; i < (1 << 4); i++) {
            System.out.println(snowFlake.nextId());
        }
    }
}
