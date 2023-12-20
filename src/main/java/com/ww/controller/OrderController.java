package com.ww.controller;

import com.ww.entity.Order;
import com.ww.dao.OrderDao;
import com.ww.service.OrderService;
import com.ww.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
//@Slf4j
public class OrderController {

    private final static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/add")
    public Integer add(@ModelAttribute("order") Order order){
        return orderDao.add(order);
    }

    @RequestMapping("/update")
    public Integer update(@ModelAttribute("order") Order order){
        return orderDao.update(order);
    }

    @RequestMapping("/delete")
    public Integer delete( Integer id){
        return orderDao.delete(id);
    }

    @RequestMapping("/selectById")
    public Order selectById(Integer id){

        log.trace("======trace");
        log.debug("======debug");
        log.info("======info");
        log.warn("======warn");
        log.error("======error");


        return orderDao.selectById(id);
    }

    @RequestMapping("/selectByParams")
    public List<Order> selectByParams(Order order){
        return orderDao.selectByParams(order);
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

    /**
     * 功能要求：新增“订单加锁、解锁”接口，实现订单删除、修改过程控制，要求（4）中订单删除、修改前进行订单粒度加锁，
     * 如果有除了当前用户之外有其他用户对相同订单id已解锁，则拒绝执行，成功加锁后执行删除、修改操作，删除操作完成后自动解锁，
     * 修改操作完成后需要手动调用解锁接口解锁，解锁过程判断是否当前用户加锁，是则解锁，否则不允许解锁；
     *
     * 从请求头获取用户名密码，通过md5加密放入request里，作为token
     * @param order
     * @return
     */
    @RequestMapping("/editOrder")
    public Integer editOrder(Order order, HttpServletRequest request) throws InterruptedException{
        String token = (String)request.getAttribute("token");

        Integer i = orderService.editOrder(token, order);

        return i;
    }

    /**
     * 新增“订单批量删除”接口，要求返回成功删除的数量、失败删除的数量、被其他用户加锁中的数量
     */
    @RequestMapping("/batchDelOrders")
    public Map<String, Integer> batchDelOrders(@RequestParam("ids") String orderIds, HttpServletRequest request){

        String token = (String)request.getAttribute("token");
        Map<String, Integer> resultMap = orderService.batchDelOrders(token, orderIds);
        return resultMap;
    }


}
