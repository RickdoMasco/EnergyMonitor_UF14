import org.springframework.data.jpa.repository.JpaRepository;

import it.energymonitor.backend.persistence.entity.UserRoleId;
import it.energymonitor.backend.persistence.entity.UserRoleEntity;


public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRole> findAllByOrderByIdAsc(); //restituisce l'intera tabella "UserRole"
    
    Optional<UserRole> findById(UserRoleId id); //restituisce, tra tutti i record della tabella "UserRole", il record la cui chiave primaria corrisponde a quella data come parametro
}
