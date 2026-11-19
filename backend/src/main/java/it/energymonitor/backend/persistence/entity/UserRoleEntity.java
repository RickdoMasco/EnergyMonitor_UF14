package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity 
@Table (name = "user_Role")
@IdClass(UserRoleId.class) //Per la chiave primaria composta
@NoArgsConstructor 
@AllArgsConstructor
@Getter 
@Setter
public class UserRoleEntity {
    
    @NotNull 
    @Id //chiave primaria composta
    @JoinColumn(name = "FKuser")
    @ManyToOne
    private UserEntity user;  //chiave esterna che punta ad utente
    
    @NotNull 
    @Id //chiave primaria composta
    @JoinColumn(name = "FKrole") 
    @ManyToOne 
    private RoleEntity role; //chiave esterna che punta a ruolo 
    
}
