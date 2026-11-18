package it.energymonitor.backend.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.UserEntity;



public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByOrderByIdAsc(); //restituisce l'intera tabella "User"

    Optional<UserEntity> findByEmail(String email); //restituisce, tra tutti i record della tabella "User", la riga la cui email corrisponde a quella data come parametro
    
}
