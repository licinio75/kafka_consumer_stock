package com.demo.kakfa.stock.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.demo.kakfa.stock.model.Pedido;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
}
