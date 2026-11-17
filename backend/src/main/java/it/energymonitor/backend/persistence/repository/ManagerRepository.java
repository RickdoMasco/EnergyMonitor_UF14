package it.energymonitor.backend.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.ManagerEntity;


public interface ManagerRepository extends JpaRepository<ManagerEntity, Long>{

    List<ManagerEntity> findAllByOrderByIdAsc(); //restituisce l'intera tabella "Manager"
    
    Optional<ManagerEntity> findByEmail(String email);  //restituisce, tra tutti i record della tabella "Manager", la riga la cui email corrisponde a quella data come parametro
}
