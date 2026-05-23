package com.jhonata.ratelimiter.repository;

import com.jhonata.ratelimiter.model.ClientQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientQuotaRepository extends JpaRepository<ClientQuota, Long> {
    Optional<ClientQuota> findByClientId(String clientId);
}
