package com.ww.controller;

import com.ww.entity.Order;
import com.ww.entity.User;
import com.ww.service.LoginService;
import com.ww.service.OrderService;
import com.ww.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
//@Slf4j
public class OrderController {

    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/add")
    public Integer add(@ModelAttribute("order") Order order){
        return orderService.add(order);
    }

    @RequestMapping("/update")
    public Integer update(@ModelAttribute("order") Order order){
        return orderService.update(order);
    }

    @RequestMapping("/delete")
    public Integer delete( Integer id){
        return orderService.delete(id);
    }

    @RequestMapping("/select")
    public Order select(Integer id){

        log.trace("======trace");
        log.debug("======debug");
        log.info("======info");
        log.warn("======warn");
        log.error("======error");


        return orderService.select();
    }

    @RequestMapping("/selectList")
    public List<Order> selectList(Order order){
        return orderService.selectList(order);
    }


    /**  以下是redis相关 **/
    /**
     * redis设置值
     * @param key
     * @param value
     * @return
     */
    @RequestMapping("/redisSet")
    public boolean redisSet(String key,String value) {

        boolean b = redisUtil.set(key, value);
        if(b){
            log.info("redis set true");
        }else{
            log.info("redis set false");
        }
        return b;
    }

    /**
     *  redis获取值
     * @param key
     * @return
     */
    @RequestMapping("/redisGet")
    public String redisGet(String key){

        Object o = redisUtil.get(key);
        if(o != null){
            return o.toString();
        }else{
            log.info(" redis get null");
            return "null";
        }
    }

}
