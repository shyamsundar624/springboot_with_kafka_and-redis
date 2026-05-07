package com.shyam.service;

import org.springframework.stereotype.Service;

import com.shyam.model.Order;
import com.shyam.producer.OrderProducer;
import com.shyam.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderProducer orderProducer;
	
	public Order createOrder(Order order) {
		Order save = orderRepository.save(order);
		
		//publish to kafka
		orderProducer.sendOrder(save);
		
		return save;
	}
}
