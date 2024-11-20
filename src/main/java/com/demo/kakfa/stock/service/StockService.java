package com.demo.kakfa.stock.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import shopsqs.demo.dto.ItemPedidoDTO;
import shopsqs.demo.dto.PedidoSQSMessageDTO;
import com.demo.kakfa.stock.model.Producto;
import com.demo.kakfa.stock.model.Pedido;
import com.demo.kakfa.stock.repository.ProductoRepository;
import com.demo.kakfa.stock.repository.PedidoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StockService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository; // Repositorio para actualizar el estado del pedido

    @KafkaListener(topics = "orders", groupId = "stock-service-group")
    public void handleOrderMessage(String message) {
        try {
            // Paso 1: Decodificar el mensaje escapado
            String decodedMessage = new ObjectMapper().readValue(message, String.class);
            
            // Paso 2: Mapear el JSON ya decodificado a PedidoSQSMessageDTO
            PedidoSQSMessageDTO pedido = new ObjectMapper().readValue(decodedMessage, PedidoSQSMessageDTO.class);

            // Paso 3: Actualizar el stock para cada item en el pedido
            for (ItemPedidoDTO item : pedido.getItems()) {
                updateStock(item.getProductoId(), item.getCantidad());
            }

            // Paso 4: Actualizar el estado del pedido a COMPLETADO
            updateOrderStatus(pedido.getPedidoId());
            
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
            System.out.println("El stock del producto: " + producto.getDescripcion() + " se acutaliza a: "+producto.getStock());
        }
    }

    private void updateOrderStatus(String pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido != null) {
            pedido.setEstado("COMPLETADO");
            pedidoRepository.save(pedido);
            System.out.println("Estado del pedido actualizado a COMPLETADO para el pedido: " + pedidoId);
        } else {
            System.err.println("Pedido no encontrado para el ID: " + pedidoId);
        }
    }
}
