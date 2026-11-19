package it.energymonitor.backend.domain;

public record Role ( 
    //nel DTO dev'essere contenuto solo il nome del ruolo, l'ID non deve arrivare al frontend
    String name      
){ }