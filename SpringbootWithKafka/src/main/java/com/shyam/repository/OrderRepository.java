package com.shyam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shyam.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
