package com.jhonata.ratelimiter.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "client_quotas")
public class ClientQuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    // limite de requisições por minuto
    private int requestsPerMinute;

    private String description;
}
