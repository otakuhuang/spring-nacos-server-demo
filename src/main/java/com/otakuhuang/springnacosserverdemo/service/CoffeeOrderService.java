package com.otakuhuang.springnacosserverdemo.service;

import com.otakuhuang.springnacosserverdemo.model.Coffee;
import com.otakuhuang.springnacosserverdemo.model.CoffeeOrder;
import com.otakuhuang.springnacosserverdemo.model.OrderState;
import com.otakuhuang.springnacosserverdemo.repository.CoffeeOrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author otaku
 * @version 1.0
 * @date 2022/2/27 23:30
 * @description description
 */
@Service
@Log4j2
@Transactional(rollbackOn = Exception.class)
public class CoffeeOrderService implements MeterBinder {

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    private Counter orderCounter = null;

    public CoffeeOrder get(Long id) {
        return coffeeOrderRepository.getById(id);
    }

    public CoffeeOrder createOrder(String customer, Coffee... coffees) {
        CoffeeOrder order = CoffeeOrder.builder()
                .customer(customer)
                .items(new ArrayList<>(Arrays.asList(coffees)))
                .state(OrderState.INIT)
                .build();
        CoffeeOrder saved = coffeeOrderRepository.save(order);
        log.info("New Order: {}", saved);
        orderCounter.increment();
        return saved;
    }

    public boolean updateState(CoffeeOrder order, OrderState state) {
        if (state.compareTo(order.getState()) <= 0) {
            log.warn("Wrong State Order: {}, {}", state, order.getState());
            return false;
        }
        order.setState(state);
        coffeeOrderRepository.save(order);
        log.info("Updated Order: {}", order);
        return true;
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        this.orderCounter = meterRegistry.counter("order.count");
    }
}
