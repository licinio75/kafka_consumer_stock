package com.demo.kakfa.stock.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.kakfa.stock.model.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String> {
}
