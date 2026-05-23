package dev.jhonata.ratelimiter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private static final int DEFAULT_LIMIT_PER_MINUTE = 60;

    public boolean isAllowed(String clientId) {
        String key = "rate:" + clientId + ":minute:" + currentMinuteBucket();
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(2));
        }
        return count != null && count <= DEFAULT_LIMIT_PER_MINUTE;
    }

    public long getCurrentCount(String clientId) {
        String key = "rate:" + clientId + ":minute:" + currentMinuteBucket();
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    public long getRemainingRequests(String clientId) {
        return Math.max(0, DEFAULT_LIMIT_PER_MINUTE - getCurrentCount(clientId));
    }

    private long currentMinuteBucket() {
        return System.currentTimeMillis() / 60_000;
    }
}
