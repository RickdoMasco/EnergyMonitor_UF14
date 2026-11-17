# Test plan — Energy Monitor

| Progetto | Team / cliente | Data | Versione |
|---|---|---|---|
| *Energy Monitor* | *TEnergy / AlpEnergia Servizi S.p.A* | *12/11/2026* | *v0.1* |


## Casi d'uso testati
 UC7, UC8 

## Matrice di test


### UC7 — Gestire edifici, dispositivi e utenti --> aggiungere un nuovo sensosore

Attore: Amministratore.  

Precondizione: Utente autenticato con ruolo amministrativo.  

Risultato osservabile: Le informazioni sono create o aggiornate, persistono dopo il riavvio e le modifiche principali sono tracciate.

| # | Requisito / vincolo | Caso di test | Input / precondizioni | Risultato atteso | Tipo | Esito |
|---|---|---|---|---|---|---|
| 1 | *L'utente dev'essere autenticato come "Amministratore"* | *utente non autenticato come "Amministratore", ma il Body della richiesta è corretto* | *Mancanza di autenticazione come "Amministratore"* | *401 Unauthorized, non dovrebbe essere possibile salvare un nuovo sensore perchè non è stata fatta nessuna procedura di login/autenticazione dell'utente* | *integration* | *fallisce (12/11) vedi bug-report-01* |
| 2 | *L'utente dev'essere autenticato come "Amministratore"* | *il dato che viene inserito ha il giusto formato (stringa) ma è "logicamente sbagliato"* | *(utente ipoteticamente autenticato come "Amministratore"); Body malformato: al posto di un indirizzo IP "veritiero" (ad esempio "192.168.1.10") viene inviato un dato totalmente diverso, ad esempio: "123ccc"*  | *422 Unprocessable Entity* | *integration* | *fallisce (12/11) vedi bug-report-02* | 
| 3 | *L'utente dev'essere autenticato come "Amministratore"* | *il dato inserito ha un formato diverso (ad esempio un intero) da quello accettato (stringa)* | *(utente ipoteticamente autenticato come "Amministratore"); Body malformato: viene inserito un numero intero (ad esempio 33333) al posto del tipo di dato corretto (ad esempio "192.168.1.10")* | *400 Bad Request* | *integration* | *fallisce (12/11) vedi bug-report-02* |
| 4 | *L'utente dev'essere autenticato come "Amministratore"* | *tentativo di salvataggio di un sensore senza inserire i dati richiesti (tentativo di inviare il Body della richiesta vuoto)* | *(utente ipoteticamente autenticato come "Amministratore"); Body malformato: Campi obbligatori "ipAddress" o/e "macAddress" assenti* | *400 Bad request* | *integration* | *fallisce (12/11) vedi bug-report-02*|

  
    


### UC8 — Verificare stato di sorgenti e componenti --> visualizzare la lista di sensori attivi
Attore: Amministratore o responsabile.  

Precondizione: Componenti applicativi avviati e raccolta dello stato abilitata, utente autenticato con ruolo amministrativo o responsabile.  

Risultato osservabile: l'amministratore o il responsabile devono essere in grado di verificare quali sensori sono attivi ed a quale edificio appartengono

| # | Requisito / vincolo | Caso di test | Input / precondizioni | Risultato atteso | Tipo | Esito |
|---|---|---|---|---|---|---|
 1 | *L'utente dev'essere autenticato come "Amministratore" o "Responsabile"*  | *utente non autenticato come "Ammministratore" o "Responsabile"* | ... | *401 Unauthorized, non dovrebbe essere possibile visualizzare la lista dei sensori attivi poiché non è stata fatta nessuna procedura di login/autenticazione dell'utente* | *integration* | *fallisce (12/11) vedi bug-report-01*|


## Note

Nessuna ambiguità riscontrata sui requisiti testati.
