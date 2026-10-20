package it.energymonitor.backend.persistence.repository;

import it.energymonitor.backend.persistence.entity.QuantityEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuantityRepository extends JpaRepository<QuantityEntity, Long> {

	Optional<QuantityEntity> findByNameIgnoreCase(String name);
}
