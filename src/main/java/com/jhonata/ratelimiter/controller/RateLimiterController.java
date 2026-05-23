package com.jhonata.ratelimiter.controller;

import com.jhonata.ratelimiter.model.ClientQuota;
import com.jhonata.ratelimiter.repository.ClientQuotaRepository;
import com.jhonata.ratelimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;
    private final ClientQuotaRepository clientQuotaRepository;

    @GetMapping("/rate-limit/check/{clientId}")
    public ResponseEntity<Map<String, Object>> checkLimit(@PathVariable String clientId) {
        boolean allowed = rateLimiterService.isAllowed(clientId);
        HttpStatus status = allowed ? HttpStatus.OK : HttpStatus.TOO_MANY_REQUESTS;

        return ResponseEntity.status(status).body(Map.of(
                "clientId", clientId,
                "allowed", allowed,
                "message", allowed ? "Requisição permitida" : "Limite de requisições atingido"
        ));
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientQuota> createClient(@RequestBody ClientQuota quota) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientQuotaRepository.save(quota));
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientQuota>> listClients() {
        return ResponseEntity.ok(clientQuotaRepository.findAll());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(rateLimiterService.getAnalytics());
    }
}
