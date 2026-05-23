package dev.jhonata.ratelimiter.controller;

import dev.jhonata.ratelimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    @GetMapping("/resource")
    public ResponseEntity<?> accessResource(@RequestHeader("X-Client-Id") String clientId) {
        boolean allowed = rateLimiterService.isAllowed(clientId);

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-RateLimit-Remaining", "0")
                    .body(Map.of("error", "Rate limit exceeded. Try again in 1 minute."));
        }

        return ResponseEntity.ok()
                .header("X-RateLimit-Remaining",
                        String.valueOf(rateLimiterService.getRemainingRequests(clientId)))
                .body(Map.of("message", "Request successful!", "clientId", clientId));
    }

    @GetMapping("/dashboard/{clientId}")
    public ResponseEntity<?> dashboard(@PathVariable String clientId) {
        return ResponseEntity.ok(Map.of(
                "clientId", clientId,
                "requestsThisMinute", rateLimiterService.getCurrentCount(clientId),
                "remainingRequests", rateLimiterService.getRemainingRequests(clientId),
                "limitPerMinute", 60
        ));
    }
}
