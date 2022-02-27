package com.otakuhuang.springnacosserverdemo.repository;

import com.otakuhuang.springnacosserverdemo.model.CoffeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {

}
