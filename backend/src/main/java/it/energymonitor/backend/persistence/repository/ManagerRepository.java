package it.energymonitor.backend.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.ManagerEntity;

public interface ManagerRepository extends JpaRepository<ManagerEntity, Long> {

    List<ManagerEntity> findAllByOrderByIdAsc();
 
}
