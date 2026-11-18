import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import it.energymonitor.backend.persistence.entity.RoleEntity;



public interface RoleRepository extends JpaRepository <RoleEntity, Long> {
    List<RoleEntity> findAllByOrderByIdAsc(); //restituisce l'intera tabella "Role"
    
    Optional <RoleEntity> findByName();  //restituisce, tra tutti i record della tabella "Role", la riga la cui email corrisponde a quella data come parametro
}
