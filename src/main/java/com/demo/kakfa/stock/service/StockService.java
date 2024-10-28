package com.demo.kakfa.stock.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.demo.kakfa.stock.dto.ItemPedidoDTO;
import com.demo.kakfa.stock.dto.PedidoSQSMessageDTO;
import com.demo.kakfa.stock.model.Producto;
import com.demo.kakfa.stock.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StockService {

    @Autowired
    private ProductoRepository productoRepository;

    @KafkaListener(topics = "orders", groupId = "stock-service-group")
    public void handleOrderMessage(PedidoSQSMessageDTO pedido) {
        for (ItemPedidoDTO item : pedido.getItems()) {
            updateStock(item.getProductoId(), item.getCantidad());
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
