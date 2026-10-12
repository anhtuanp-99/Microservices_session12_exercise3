package com.pharmacy.inventoryservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellRequest {

    @NotNull(message = "medicineId không được để trống")
    @Min(value = 1, message = "medicineId phải lớn hơn 0")
    private Long medicineId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}