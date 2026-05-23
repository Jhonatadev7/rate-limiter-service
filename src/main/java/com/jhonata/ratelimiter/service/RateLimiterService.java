package com.jhonata.ratelimiter.service;

import com.jhonata.ratelimiter.model.ClientQuota;
import com.jhonata.ratelimiter.repository.ClientQuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ClientQuotaRepository quotaRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${rate-limiter.window-seconds}")
    private int windowSeconds;

    /**
     * Verifica se o cliente pode fazer uma nova requisição.
     * Incrementa o contador no Redis e compara com a quota do PostgreSQL.
     */
    public boolean isAllowed(String clientId) {
        ClientQuota quota = quotaRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clientId));

        String redisKey = "rate_limit:" + clientId;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        // Define o TTL apenas na primeira requisição da janela
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        return currentCount != null && currentCount <= quota.getRequestsPerMinute();
    }

    /**
     * Retorna analytics de uso para todos os clientes.
     */
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        quotaRepository.findAll().forEach(client -> {
            String redisKey = "rate_limit:" + client.getClientId();
            String countStr = redisTemplate.opsForValue().get(redisKey);
            long used = countStr != null ? Long.parseLong(countStr) : 0;

            analytics.put(client.getClientId(), Map.of(
                    "quota", client.getRequestsPerMinute(),
                    "used", used,
                    "remaining", Math.max(0, client.getRequestsPerMinute() - used)
            ));
        });

        return analytics;
    }
}
