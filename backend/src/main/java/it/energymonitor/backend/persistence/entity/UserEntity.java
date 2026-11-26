package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import lombok.Setter;

@Entity 
@Table(name = "users")
@Getter
@NoArgsConstructor  
@Setter 
public class UserEntity {

    @Id
    @Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)// la gestione dell'autoincrement viene affidata al DB
	private Long id;

	@Column(name = "nome")
	@NotNull 
    private String nome;
    
    @Column(name = "cognome")
    @NotNull 
    private String cognome;
    
    @Column(name = "email", unique = true)
    @NotNull 
    private String email;
    
    @Column(name = "PasswordHash")
    @NotNull 
    private String passwordHash;
   
    public UserEntity(String nome, String cognome, String email, String passwordHash) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
