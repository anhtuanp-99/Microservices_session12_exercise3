package com.pharmacy.inventoryservice.consumer;

import com.pharmacy.inventoryservice.event.OrderCreatedEvent;
import com.pharmacy.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void onOrderCreated(
            @Payload OrderCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("┌─ [INVENTORY-CONSUMER] partition={}, offset={}, orderCode={}",
                partition, offset, event.getOrderCode());

        try {
            inventoryService.processOrderCreated(event);
            log.info("└─ [INVENTORY] OK");
        } catch (Exception e) {
            log.error("└─ [INVENTORY] Lỗi: {}", e.getMessage(), e);
        }
    }
}