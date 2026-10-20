package it.energymonitor.backend.persistence.repository;

import it.energymonitor.backend.persistence.entity.MeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long> {
}
