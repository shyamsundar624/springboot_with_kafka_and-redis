package com.shyam.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.shyam.model.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	private static final String TOPIC = "order-topic";

	public void sendOrder(Order order) {
		kafkaTemplate.send(TOPIC, order);
	}
}
