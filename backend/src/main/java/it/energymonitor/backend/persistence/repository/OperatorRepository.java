package it.energymonitor.backend.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.OperatorEntity;

public interface OperatorRepository extends JpaRepository<OperatorEntity, Long> {

    List<OperatorEntity> findAllByOrderByIdAsc(); //restituisce l'intera tabella "Operator"

    Optional<OperatorEntity> findByEmail(String email); //restituisce, tra tutti i record della tabella "Operator", la riga la cui email corrisponde a quella data come parametro
}
