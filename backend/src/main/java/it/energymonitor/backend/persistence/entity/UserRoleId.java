package it.energymonitor.backend.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {
    // I nomi devono coincidere con gli attributi di UserRoleEntity, ma il tipo è quello dell'ID
    private Long user;
    private Long role;
}