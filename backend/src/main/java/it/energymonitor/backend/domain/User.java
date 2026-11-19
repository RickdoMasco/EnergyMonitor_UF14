package it.energymonitor.backend.domain;

public record User(
    long Id,
    String nome,
    String cognome,
    String email,
    String password ) {
}
