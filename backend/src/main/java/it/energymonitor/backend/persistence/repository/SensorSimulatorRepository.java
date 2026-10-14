package it.energymonitor.backend.persistence.repository;

import it.energymonitor.backend.persistence.entity.SensorSimulatorEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorSimulatorRepository extends JpaRepository<SensorSimulatorEntity, Long> {

	List<SensorSimulatorEntity> findAllByOrderByIdAsc();
}
