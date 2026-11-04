package it.energymonitor.backend.persistence.entity;

import it.energymonitor.backend.domain.ValidationLimits;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "quantities")
@Getter
public class QuantityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = ValidationLimits.QUANTITY_MAX_LENGTH)
	private String name;

	protected QuantityEntity() {
	}

	public QuantityEntity(String name) {
		this.name = name;
	}
}
