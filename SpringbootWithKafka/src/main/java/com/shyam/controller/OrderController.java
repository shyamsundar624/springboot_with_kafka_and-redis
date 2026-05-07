package com.shyam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.model.Order;
import com.shyam.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	  private final OrderService service;

	    @PostMapping
	    public Order create(@RequestBody Order order) {
	        return service.createOrder(order);
	    }
	    
	    @GetMapping("/{id}")
	    public Order getOrder(@PathVariable Long id) {
	        return service.getOrderById(id);
	    }
}
