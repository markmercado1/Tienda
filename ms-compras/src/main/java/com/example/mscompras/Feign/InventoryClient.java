package com.example.mscompras.Feign;

import com.example.mscompras.Dto.StockUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario-service", path = "/inventory")
public interface InventoryClient {
    @PostMapping("/inventory/update")
    void updateStock(@RequestBody StockUpdateDto dto);
}