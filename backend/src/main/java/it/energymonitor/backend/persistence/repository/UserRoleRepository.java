package it.energymonitor.backend.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import it.energymonitor.backend.persistence.entity.UserRoleId;
import it.energymonitor.backend.persistence.entity.UserRoleEntity;


public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findAll(); //restituisce l'intera tabella "UserRole"
    
    Optional<UserRoleEntity> findById(UserRoleId id); //restituisce, tra tutti i record della tabella "UserRole", il record la cui chiave primaria corrisponde a quella data come parametro
}
