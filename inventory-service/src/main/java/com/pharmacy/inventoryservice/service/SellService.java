package com.pharmacy.inventoryservice.service;

import com.pharmacy.inventoryservice.dto.request.SellRequest;
import com.pharmacy.inventoryservice.dto.response.SellResponse;
import com.pharmacy.inventoryservice.entity.Medicine;
import com.pharmacy.inventoryservice.exception.LockAcquisitionException;
import com.pharmacy.inventoryservice.exception.ResourceNotFoundException;
import com.pharmacy.inventoryservice.gateway.StockLockGateway;
import com.pharmacy.inventoryservice.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellService {

    private static final String LOCK_KEY_PREFIX = "lock:product:";

    private final MedicineRepository medicineRepository;
    private final StockLockGateway lockGateway;   // ← inject Gateway

    public SellResponse sell(SellRequest request) {
        Long medicineId = request.getMedicineId();
        String lockKey = LOCK_KEY_PREFIX + medicineId;
        String threadName = Thread.currentThread().getName();

        try {
            return lockGateway.executeWithLock(lockKey, () -> {
                // Toàn bộ business logic nằm trong lambda này
                // Không cần quan tâm tryLock/unlock
                Medicine medicine = medicineRepository.findById(medicineId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Medicine", "ID", medicineId));

                if (medicine.getStockQuantity() < request.getQuantity()) {
                    return SellResponse.builder()
                            .success(false)
                            .message("Hết hàng. Còn " + medicine.getStockQuantity())
                            .medicineId(medicineId)
                            .remainingStock(medicine.getStockQuantity())
                            .threadName(threadName)
                            .build();
                }

                medicine.setStockQuantity(
                        medicine.getStockQuantity() - request.getQuantity());
                medicineRepository.save(medicine);

                return SellResponse.builder()
                        .success(true)
                        .message("Bán thuốc thành công")
                        .medicineId(medicineId)
                        .quantity(request.getQuantity())
                        .remainingStock(medicine.getStockQuantity())
                        .threadName(threadName)
                        .build();
            });

        } catch (LockAcquisitionException e) {
            return SellResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .medicineId(medicineId)
                    .threadName(threadName)
                    .build();
        }
    }
}