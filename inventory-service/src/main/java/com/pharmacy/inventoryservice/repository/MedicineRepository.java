package com.pharmacy.inventoryservice.repository;

import com.pharmacy.inventoryservice.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Medicine m SET m.stockQuantity = m.stockQuantity - :qty " +
            "WHERE m.id = :id AND m.stockQuantity >= :qty")
    int decreaseStock(@Param("id") Long id, @Param("qty") Integer qty);
}