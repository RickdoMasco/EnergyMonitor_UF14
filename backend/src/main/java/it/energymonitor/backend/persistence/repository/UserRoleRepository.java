import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.UserRoleId;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRole> findAllByOrderByIdAsc(); //restituisce l'intera tabella "UserRole"
    
    Optional<UserRole> findByUser(); //restituisce, tra tutti i record della tabella "Use", la riga la cui email corrisponde a quella data come parametro

}
