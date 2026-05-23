package dev.jhonata.ratelimiter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client_quotas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    private int requestsPerMinute;
    private int requestsPerHour;
    private int requestsPerDay;
}
