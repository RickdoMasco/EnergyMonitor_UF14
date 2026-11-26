package it.energymonitor.backend.domain;

/**
 * Lunghezze massime condivise tra validazione delle richieste e colonne del database.
 */
public final class ValidationLimits {

	public static final int QUANTITY_MAX_LENGTH = 100;
	public static final int UNIT_MAX_LENGTH = 32;
	public static final int OUTCOME_REASON_MAX_LENGTH = 255;

	private ValidationLimits() {
	}
}
