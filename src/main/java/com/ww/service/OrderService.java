package com.ww.service;

import com.ww.dao.OrderDao;
import com.ww.entity.Order;
import com.ww.utils.RedisUtil;
import jodd.util.StringUtil;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired(required = false)
    private RedissonClient redissonClient;


    public Integer editOrder(String token, Order order) throws InterruptedException{

        String lockKey = token+"_"+ order.getNO();
        int i = 0;

        /**
         * sync方法，可以多个用户同时操作,不能释放锁
         */
        /*synchronized (lockKey){
            log.info("lock by sync");

            i = orderDao.update(order);
            log.info("order update count:"+i);

            Thread.sleep(3 * 1000);
        }*/

        /**
         * ReetrantLock方法，不能多个用户同时操作，
         * 持续等待吗？
         */
        /*Lock lock = new ReentrantLock();
        lock.lock();

        i = orderDao.update(order);
        log.info("order update count:" + i);
        Thread.sleep(3 * 1000);

        lock.unlock();*/

        /**
         * 用redis的锁实现
         */
       /* boolean setFlag = true;
       //此处版本问题：最新spring-boot-starter-data-redis的最新版本带的spring-data-redis都是2.0.9
       //redis从2.1以上开始能删除设置有效期,放弃
//        boolean setFlag = redisUtil.setIfAbsent(lockKey, 5*1000, TimeUnit.MILLISECONDS);
        if(setFlag){
            try{
                i = orderDao.update(order);
                log.info("order update count:" + i);
                Thread.sleep(3 * 1000);
            }finally {
                //不能直接删除，因为锁超时失效可能把其他进程的锁删除
                //redisUtil.del(lockKey);

                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long delFlag = redisUtil.delByScript(script, lockKey);
                log.info("del redis lockkey:" + delFlag);
            }
        }else{
            //做什么处理 隔一段时间循环获取，此处省略
            log.info("redis get lock error");
        }

       // if(lockKey)
        redisUtil.del(lockKey);*/


        //用redission锁
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = lock.isLocked();

        try{
            if(!locked){//如果锁没有被占用，才可以继续加锁，否则什么都不执行
                locked = lock.tryLock(1*1000, 4*60*1000, TimeUnit.MILLISECONDS);
                if(locked){
                    log.info("lockkey:"+lockKey);
                    log.info("get redis lock success");
                    i = orderDao.update(order);
                    Thread.sleep(3*60*1000);
                }else{
                    log.info("get redis lock failed");
                }
            }else{
                log.error("锁被占用，执行失败");
                i=-1;
            }

        }catch (InterruptedException e){
            e.printStackTrace();
            log.info("get redis lock exception");
        }finally {
            if(!locked){
                log.info("get redis lock failed");
            }

            //如果锁是锁定状态且被当前线程所持有，才可以解锁
            if(locked && lock.isHeldByCurrentThread()){
                lock.unlock();
            }

        }


        return i;
    }

    /**
     * 要求返回成功删除的数量、失败删除的数量、被其他用户加锁中的数量
     * @param orderIds
     * @param token
     * @return
     */
    public Map<String, Integer> batchDelOrders(String token, String orderIds){
        int delSucAmt = 0;
        int delFailAmt = 0;
        int lockedAmt = 0;

        //分割字符串
        if(!StringUtils.isEmpty(orderIds)){
            String [] idArray = orderIds.split(",");
            if(idArray != null && idArray.length > 0){
                for(String id : idArray){
                    Order order = orderDao.selectById(Integer.valueOf(id));
                    if(order != null){
                        String lockKey = token+"_"+ order.getNO();
                        boolean lockedFlg = redissonClient.getKeys().getKeysByPattern(lockKey).iterator().hasNext();
                        log.info("lockKey:"+lockKey+" lockedFlg="+lockedFlg);
                        if(lockedFlg){
                            lockedAmt ++;
                        }else{
                            //没被锁的订单才可以删除
                            if(orderDao.delete(Integer.valueOf(id)) > 0){
                                delSucAmt ++;
                            }else{
                                delFailAmt ++;
                            }
                        }
                    }
                }
            }
        }


        Map<String,Integer> resultMap = new HashMap<>();
        resultMap.put("delSucAmt",delSucAmt);
        resultMap.put("delFailAmt",delFailAmt);
        resultMap.put("lockedAmt",lockedAmt);

        return resultMap;
    }

}
