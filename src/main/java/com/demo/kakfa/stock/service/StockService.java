package com.demo.kakfa.stock.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import shopsqs.demo.dto.ItemPedidoDTO;
import com.demo.kakfa.stock.model.Producto;
import com.demo.kakfa.stock.repository.ProductoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import shopsqs.demo.dto.PedidoSQSMessageDTO;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StockService {

    @Autowired
    private ProductoRepository productoRepository;

    @KafkaListener(topics = "orders", groupId = "stock-service-group6")
    public void handleOrderMessage(String message) {
        System.out.println("Mensaje recibido de Kafka: " + message);
        try {
            PedidoSQSMessageDTO pedido = new ObjectMapper().readValue(message, PedidoSQSMessageDTO.class);
            for (ItemPedidoDTO item : pedido.getItems()) {
                updateStock(item.getProductoId(), item.getCantidad());
            }
        } catch (JsonMappingException e) {
            System.err.println("Error al mapear el JSON: " + e.getPathReference());
        } catch (JsonProcessingException e) {
            System.err.println("Error en el procesamiento del JSON: " + e.getOriginalMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar el mensaje: " + e.getMessage());
        }
    }

    private void updateStock(String productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            int nuevoStock = producto.getStock() - cantidad;
            producto.setStock(nuevoStock >= 0 ? nuevoStock : 0);
            productoRepository.save(producto);
        }
    }
}
