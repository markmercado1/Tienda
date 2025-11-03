package com.example.msventas.feign;

import com.example.msventas.Dto.StockUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventory", url = "${services.inventory.url}")
public interface InventoryClient {
    @PostMapping("/api/inventory/update")
    void updateStock(@RequestBody StockUpdateDto dto);
}