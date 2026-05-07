package com.shyam.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.shyam.model.Order;
import com.shyam.producer.OrderProducer;
import com.shyam.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	 private final OrderRepository repository;
	    private final OrderProducer producer;
	    
	    private final RedisTemplate<String, Object> redisTemplate;

	    private static final String CACHE_PREFIX = "ORDER_";

	    public Order getOrderById(Long id) {

	        String key = CACHE_PREFIX + id;

	        // ✅ 1. Check Redis
	        Order cachedOrder = (Order) redisTemplate.opsForValue().get(key);

	        if (cachedOrder != null) {
	            System.out.println("✅ Fetched from Redis");
	            return cachedOrder;
	        }

	        // ❌ 2. Fetch from DB
	        System.out.println("❌ Fetching from DB");

	        Order order = repository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Order not found"));

	        // ✅ 3. Store in Redis
	        redisTemplate.opsForValue().set(key, order);

	        return order;
	    }

	    public Order createOrder(Order order) {
	        Order saved = repository.save(order);

	        // Publish to Kafka
	        producer.sendOrder(saved);

	        return saved;
	    }
	    
}
