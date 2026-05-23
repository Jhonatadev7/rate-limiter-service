package com.jhonatabreno.ratelimiter.repository;

import com.jhonatabreno.ratelimiter.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);
}
