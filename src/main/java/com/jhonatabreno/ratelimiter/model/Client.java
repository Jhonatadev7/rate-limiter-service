package com.jhonatabreno.ratelimiter.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    private String name;
    private int limitPerMinute;
    private int limitPerHour;
    private boolean blocked;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.limitPerMinute == 0) this.limitPerMinute = 10;
        if (this.limitPerHour == 0) this.limitPerHour = 100;
    }
}
