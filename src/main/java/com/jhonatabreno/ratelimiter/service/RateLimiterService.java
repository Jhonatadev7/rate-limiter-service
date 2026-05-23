package com.jhonatabreno.ratelimiter.service;

import com.jhonatabreno.ratelimiter.model.Client;
import com.jhonatabreno.ratelimiter.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redis;
    private final ClientRepository clientRepository;

    public Map<String, Object> checkLimit(String clientId) {
        Optional<Client> opt = clientRepository.findByClientId(clientId);
        int limitMin  = opt.map(Client::getLimitPerMinute).orElse(10);
        int limitHour = opt.map(Client::getLimitPerHour).orElse(100);

        String keyMin  = "rl:" + clientId + ":min";
        String keyHour = "rl:" + clientId + ":hour";

        Long countMin  = redis.opsForValue().increment(keyMin);
        Long countHour = redis.opsForValue().increment(keyHour);

        if (countMin  != null && countMin  == 1) redis.expire(keyMin,  Duration.ofMinutes(1));
        if (countHour != null && countHour == 1) redis.expire(keyHour, Duration.ofHours(1));

        boolean allowed = countMin <= limitMin && countHour <= limitHour;

        Map<String, Object> result = new HashMap<>();
        result.put("clientId", clientId);
        result.put("allowed", allowed);
        result.put("requestsThisMinute", countMin);
        result.put("requestsThisHour", countHour);
        result.put("limitPerMinute", limitMin);
        result.put("limitPerHour", limitHour);
        return result;
    }

    public Map<String, Object> getAnalytics(String clientId) {
        String valMin  = redis.opsForValue().get("rl:" + clientId + ":min");
        String valHour = redis.opsForValue().get("rl:" + clientId + ":hour");

        Map<String, Object> map = new HashMap<>();
        map.put("clientId", clientId);
        map.put("requestsThisMinute", valMin  != null ? Long.parseLong(valMin)  : 0);
        map.put("requestsThisHour",   valHour != null ? Long.parseLong(valHour) : 0);
        return map;
    }
}
