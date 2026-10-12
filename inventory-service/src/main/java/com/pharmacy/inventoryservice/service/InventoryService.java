package com.pharmacy.inventoryservice.service;

import com.pharmacy.inventoryservice.event.OrderCreatedEvent;
import com.pharmacy.inventoryservice.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final MedicineRepository medicineRepository;

    @Transactional
    public void processOrderCreated(OrderCreatedEvent event) {
        log.info(">>> [INVENTORY] Xử lý đơn: orderCode={}, medicineId={}, qty={}",
                event.getOrderCode(), event.getMedicineId(), event.getQuantity());

        int updated = medicineRepository.decreaseStock(
                event.getMedicineId(), event.getQuantity());

        if (updated == 0) {
            log.error("<<< [INVENTORY] Không đủ hàng: medicineId={}",
                    event.getMedicineId());
            return;
        }

        log.info("<<< [INVENTORY] Đã trừ stock thành công: medicineId={}, qty={}",
                event.getMedicineId(), event.getQuantity());
    }
}