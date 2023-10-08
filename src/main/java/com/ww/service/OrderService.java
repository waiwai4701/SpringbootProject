package com.ww.service;

import com.ww.entity.Order;
import com.ww.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

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

    public Order select(){
        return orderMapper.select();
    }

    public List<Order> selectList(Order order){
       return orderMapper.selectList(order);
    }
}
