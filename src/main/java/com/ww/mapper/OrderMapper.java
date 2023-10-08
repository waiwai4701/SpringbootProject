package com.ww.mapper;

import com.ww.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface OrderMapper {

    Integer add(Order order);

    Integer update(Order order);

    Integer delete(Integer id);

    Order select();

    List<Order> selectList(Order order);
}
