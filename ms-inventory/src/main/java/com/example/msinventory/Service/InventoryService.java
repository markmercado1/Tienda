package com.example.msinventory.Service;

import com.example.msinventory.Dto.InventoryDto;
import com.example.msinventory.Dto.MovementDto;
import com.example.msinventory.Dto.ProductDto;
import com.example.msinventory.Dto.StockUpdateDto;
import com.example.msinventory.Entity.Inventory;
import com.example.msinventory.Entity.InventoryMovement;
import com.example.msinventory.Repository.InventoryMovementRepository;
import com.example.msinventory.Repository.InventoryRepository;
import com.example.msinventory.feign.ProductClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductClient productClient;

    public InventoryDto getStock(String productSku) {
        return inventoryRepository.findByProductSku(productSku)
            .map(this::toDto)
            .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    @Transactional
    public void updateStock(StockUpdateDto dto) {
        ProductDto product = productClient.getBySku(dto.productSku());
        if (product == null) {
            throw new RuntimeException("Producto no encontrado en ms-products");
        }
        Inventory inventory = inventoryRepository.findByProductSku(dto.productSku())
            .orElseGet(() -> {
                Inventory inv = new Inventory();
                inv.setProductSku(dto.productSku());
                inv.setQuantity(0);
                return inv;
            });

        if (dto.type() == InventoryMovement.MovementType.ENTRADA) {
            inventory.setQuantity(inventory.getQuantity() + dto.quantity());
        } else {
            inventory.setQuantity(inventory.getQuantity() - dto.quantity());
        }

        inventoryRepository.save(inventory);

        InventoryMovement movement = new InventoryMovement();
        movement.setProductSku(dto.productSku());
        movement.setQuantity(dto.quantity());
        movement.setType(dto.type());
        movement.setReference(dto.reference());
        movement.setReason(dto.reason());
        movementRepository.save(movement);
    }

    public List<MovementDto> getMovements(String productSku) {
        return movementRepository.findByProductSkuOrderByCreatedAtDesc(productSku)
            .stream()
            .map(this::toMovementDto)
            .toList();
    }

    private InventoryDto toDto(Inventory i) {
        return new InventoryDto(i.getId(), i.getProductSku(), i.getLocation(), i.getQuantity());
    }

    private MovementDto toMovementDto(InventoryMovement m) {
        return new MovementDto(m.getId(), m.getProductSku(), m.getQuantity(), 
            m.getType(), m.getReference(), m.getReason(), m.getCreatedAt());
    }
}