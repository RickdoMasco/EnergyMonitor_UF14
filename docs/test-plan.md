# Test plan — Energy Monitor

| Progetto         | Team / cliente                       | Data         | Versione |
| ---------------- | ------------------------------------ | ------------ | -------- |
| _Energy Monitor_ | _TEnergy / AlpEnergia Servizi S.p.A_ | _12/11/2026_ | _v0.1_   |

## Casi d'uso testati

UC7, UC8

## Matrice di test

### UC7 — Gestire edifici, dispositivi e utenti --> aggiungere un nuovo sensosore

Attore: Amministratore.

Precondizione: Utente autenticato con ruolo amministrativo.

Risultato osservabile: Le informazioni sono create o aggiornate, persistono dopo il riavvio e le modifiche principali sono tracciate.

| #   | Requisito / vincolo                                     | Caso di test                                                                                                                  | Input / precondizioni                                                                                                                                                                                            | Risultato atteso                                                                                                                                          | Tipo          | Esito                                 |
| --- | ------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- | ------------------------------------- |
| 1   | _L'utente dev'essere autenticato come "Amministratore"_ | _utente non autenticato come "Amministratore", ma il Body della richiesta è corretto_                                         | _Mancanza di autenticazione come "Amministratore"_                                                                                                                                                               | _401 Unauthorized, non dovrebbe essere possibile salvare un nuovo sensore perchè non è stata fatta nessuna procedura di login/autenticazione dell'utente_ | _integration_ | _fallisce (12/11) vedi bug-report-01_ |
| 2   | _L'utente dev'essere autenticato come "Amministratore"_ | _il dato che viene inserito ha il giusto formato (stringa) ma è "logicamente sbagliato"_                                      | _(utente ipoteticamente autenticato come "Amministratore"); Body malformato: al posto di un indirizzo IP "veritiero" (ad esempio "192.168.1.10") viene inviato un dato totalmente diverso, ad esempio: "123ccc"_ | _422 Unprocessable Entity_                                                                                                                                | _integration_ | _fallisce (12/11) vedi bug-report-02_ |
| 3   | _L'utente dev'essere autenticato come "Amministratore"_ | _il dato inserito ha un formato diverso (ad esempio un intero) da quello accettato (stringa)_                                 | _(utente ipoteticamente autenticato come "Amministratore"); Body malformato: viene inserito un numero intero (ad esempio 33333) al posto del tipo di dato corretto (ad esempio "192.168.1.10")_                  | _400 Bad Request_                                                                                                                                         | _integration_ | _fallisce (12/11) vedi bug-report-02_ |
| 4   | _L'utente dev'essere autenticato come "Amministratore"_ | _tentativo di salvataggio di un sensore senza inserire i dati richiesti (tentativo di inviare il Body della richiesta vuoto)_ | _(utente ipoteticamente autenticato come "Amministratore"); Body malformato: Campi obbligatori "ipAddress" o/e "macAddress" assenti_                                                                             | _400 Bad request_                                                                                                                                         | _integration_ | _fallisce (12/11) vedi bug-report-02_ |

### UC8 — Verificare stato di sorgenti e componenti --> visualizzare la lista di sensori attivi

Attore: Amministratore o responsabile.

Precondizione: Componenti applicativi avviati e raccolta dello stato abilitata, utente autenticato con ruolo amministrativo o responsabile.

Risultato osservabile: l'amministratore o il responsabile devono essere in grado di verificare quali sensori sono attivi ed a quale edificio appartengono

| #   | Requisito / vincolo                                                      | Caso di test                                                     | Input / precondizioni | Risultato atteso                                                                                                                                                          | Tipo          | Esito                                 |
| --- | ------------------------------------------------------------------------ | ---------------------------------------------------------------- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- | ------------------------------------- |
| 1   | _L'utente dev'essere autenticato come "Amministratore" o "Responsabile"_ | _utente non autenticato come "Ammministratore" o "Responsabile"_ | ...                   | _401 Unauthorized, non dovrebbe essere possibile visualizzare la lista dei sensori attivi poiché non è stata fatta nessuna procedura di login/autenticazione dell'utente_ | _integration_ | _fallisce (12/11) vedi bug-report-01_ |

## Note

Nessuna ambiguità riscontrata sui requisiti testati.
