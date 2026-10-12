package com.pharmacy.inventoryservice.gateway;

import com.pharmacy.inventoryservice.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Gateway cung cấp cơ chế lock phân tán.
 *
 * Ưu điểm: Business logic không cần quan tâm đến try/finally unlock.
 * Chỉ cần gọi executeWithLock(key, action) và truyền đoạn logic cần bảo vệ.
 *
 * @param <T> kiểu dữ liệu trả về của action
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockLockGateway {

    private static final long WAIT_TIME_SECONDS = 3;
    private static final long LEASE_TIME_SECONDS = 5;

    private final RedissonClient redissonClient;

    /**
     * Thực thi action trong vùng critical của lock.
     *
     * @param lockKey key lock (ví dụ: "lock:product:1")
     * @param action  đoạn logic cần bảo vệ
     * @return kết quả từ action
     * @throws LockAcquisitionException nếu không acquire được lock trong waitTime
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockKey);
        String threadName = Thread.currentThread().getName();

        boolean locked = false;
        try {
            log.info("[{}] Cố acquire lock: {}", threadName, lockKey);

            locked = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("[{}] Không acquire được lock sau {}s: {}",
                        threadName, WAIT_TIME_SECONDS, lockKey);
                throw new LockAcquisitionException(
                        "Hệ thống đang xử lý, vui lòng thử lại sau");
            }

            log.info("[{}] ✅ Acquired lock: {}", threadName, lockKey);
            return action.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException("Yêu cầu bị gián đoạn");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[{}] 🔓 Released lock: {}", threadName, lockKey);
            }
        }
    }
}