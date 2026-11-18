package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity 
@Table(name = "user")
@Getter
public class UserEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome")
	@NotNull 
    private String nome;
    
    @Column(name = "cognome")
    @NotNull 
    private String cognome;
    
    @Column(name = "email")
    @NotNull 
    private String email;
    
    @Column(name = "PasswordHash")
    @NotNull 
    private String PasswordHash; 
    
    @ManyToOne 
    @JoinColumn(name = "role_id")
    private Long FKruolo; 
    

    public AdministratorEntity(String nome, String cognome, String email, String PasswordHash) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.PasswordHash = PasswordHash;
    }
}
