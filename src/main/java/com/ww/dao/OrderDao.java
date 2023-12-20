package com.ww.dao;

import com.ww.entity.Order;
import com.ww.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDao {

    @Autowired
    OrderMapper orderMapper;

    public Integer add(Order order){
        return orderMapper.add(order);
    }

    public Integer update(Order order){
        return orderMapper.update(order);
    }

    public Integer delete(Integer id){
        return orderMapper.delete(id);
    }

    public Order selectById(Integer id){
        return orderMapper.selectById(id);
    }

    public  List<Order> selectByParams(Order order){
        return orderMapper.selectByParams(order);
    }

}
