package com.pharmacy.inventoryservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellResponse {
    private boolean success;
    private String message;
    private Long medicineId;
    private Integer quantity;
    private Integer remainingStock;
    private String threadName;
}