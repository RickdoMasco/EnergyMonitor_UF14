package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity 
@Table(name = "Manager");
@Getter
public class ManagerEntity{

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String nome;
    private String cognome;
    private String email;

    public ManagerEntity(String nome, String cognome, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }
}