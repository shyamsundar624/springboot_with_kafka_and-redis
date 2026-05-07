package com.shyam.consumer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.shyam.model.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
	private final RedisTemplate<String, Object> redisTemplate;

	@KafkaListener(topics = "order-topic", groupId = "order-group")
	public void consume(Order order) {
		log.info("Consumed Order: {}", order);

		// Save to Redis Cache
		redisTemplate.opsForValue().set("ORDER_" + order.getId(), order);
	}
}
