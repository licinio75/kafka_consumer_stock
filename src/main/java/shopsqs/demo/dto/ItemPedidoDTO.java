package shopsqs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private String productoId;  // ID del producto
    private String nombreProducto; // Nombre del producto
    private int cantidad; // Cantidad de productos
    private double precioUnitario; // Precio unitario del producto
    private double precioTotal; // Precio total del producto (cantidad * precioUnitario)
}
