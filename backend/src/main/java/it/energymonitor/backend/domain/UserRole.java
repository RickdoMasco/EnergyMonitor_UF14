package it.energymonitor.backend.domain;

import it.energymonitor.backend.persistence.entity.RoleEntity;
import it.energymonitor.backend.persistence.entity.UserEntity;

public record UserRole(
    User user,
    Role role
) {

}
