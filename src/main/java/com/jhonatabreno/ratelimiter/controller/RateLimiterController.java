package com.jhonatabreno.ratelimiter.controller;

import com.jhonatabreno.ratelimiter.model.Client;
import com.jhonatabreno.ratelimiter.repository.ClientRepository;
import com.jhonatabreno.ratelimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;
    private final ClientRepository clientRepository;

    @PostMapping("/check/{clientId}")
    public ResponseEntity<Map<String, Object>> check(@PathVariable String clientId) {
        Map<String, Object> result = rateLimiterService.checkLimit(clientId);
        boolean allowed = (boolean) result.get("allowed");
        return allowed ? ResponseEntity.ok(result) : ResponseEntity.status(429).body(result);
    }

    @GetMapping("/analytics/{clientId}")
    public ResponseEntity<Map<String, Object>> analytics(@PathVariable String clientId) {
        return ResponseEntity.ok(rateLimiterService.getAnalytics(clientId));
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> list() {
        return ResponseEntity.ok(clientRepository.findAll());
    }

    @PostMapping("/clients")
    public ResponseEntity<Client> create(@RequestBody Client client) {
        return ResponseEntity.ok(clientRepository.save(client));
    }
}
