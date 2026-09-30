# Backlog iniziale — Energy Monitor / AlpEnergia Servizi S.p.A.

|             |                                                                                                         |
| ----------- | ------------------------------------------------------------------------------------------------------- |
| **Team**    | TEnergy: Alessandro Passerini, Riccardo Mascotto, Giacomo Grattarola, Emanuele Rossi, Sebastiano Dalpez |
| **Cliente** | AlpEnergia Servizi S.p.A.                                                                               |
| **Data**    | 30/09/2026                                                                                              |

## Voci

### 1. Ingestione indipendente e persistente

Come responsabile tecnico voglio acquisire misure da più dispositivi tramite un'interfaccia definita, per dimostrare che le sorgenti sono indipendenti dalla dashboard.

**Fatto quando:**

- Il simulatore invia misure per più dispositivi registrati.
- Ogni misura valida è persistita con dispositivo, grandezza, valore, unità e timestamp.
- Il guasto di una sorgente non impedisce la consultazione dei dati già salvati né l'invio dalle altre sorgenti.

### 2. Gestione di edifici e dispositivi

Come amministratore voglio associare i dispositivi agli edifici e configurarne le grandezze, per organizzare correttamente le misure.

**Fatto quando:**

- Posso creare e modificare edifici e dispositivi con identificativi univoci.
- Un dispositivo è associato a un edificio e può essere disattivato senza cancellare lo storico.
- Le configurazioni persistono al riavvio e non sono codificate nel sorgente.

### 3. Validazione e tracciamento dell'acquisizione

Come responsabile voglio sapere quali misure sono state accettate o rifiutate, per diagnosticare problemi di sorgente.

**Fatto quando:**

- Payload incompleti, non interpretabili o riferiti a dispositivi sconosciuti ricevono un esito di rifiuto.
- I log riportano esito e sorgente senza esporre credenziali.
- Lo stato della singola sorgente è distinguibile dallo stato generale della piattaforma.

### 4. Consultazione di valori recenti e trend

Come operatore energetico voglio filtrare le misure per edificio, dispositivo, grandezza e periodo, per analizzarne l'andamento.

**Fatto quando:**

- Posso consultare ultimo valore e storico per i filtri supportati.
- I dati di prova coprono un intervallo temporale e più dispositivi sufficienti a mostrare trend.
- L'indisponibilità del simulatore non nasconde i dati già persistiti.

### 5. Soglie configurabili e audit

Come responsabile voglio configurare soglie senza modificare il codice, per adattare il monitoraggio alle esigenze concordate.

**Fatto quando:**

- Posso creare e modificare una soglia per le grandezze e gli ambiti supportati.
- Ogni modifica registra autore, data/ora e valori precedenti/nuovi.
- Le soglie persistono al riavvio e la semantica del superamento è documentata.

### 6. Generazione e gestione anomalie

Come operatore voglio vedere gli eventi generati dai superamenti, per individuare le situazioni da verificare.

**Fatto quando:**

- Una misura che supera una soglia attiva genera un'anomalia collegata a misura e soglia.
- L'anomalia mostra grandezza, valore, soglia e timestamp.
- La presa visione è registrata con utente e data/ora senza alterare la misura.

### 7. Dashboard per edificio e gruppo

Come operatore energetico voglio una vista sintetica per edificio o gruppo di dispositivi, per capire rapidamente lo stato.

**Fatto quando:**

- Posso selezionare l'ambito e vedere indicatori recenti e anomalie pertinenti.
- I dati provengono dalle API, non da valori simulati incorporati nell'interfaccia.
- La dashboard comunica l'assenza o la non aggiornatezza dei dati.

### 8. Autenticazione e ruoli

Come amministratore voglio distinguere le funzioni di operatore, responsabile e amministratore, per limitare le modifiche agli utenti autorizzati.

**Fatto quando:**

- Le funzioni protette richiedono autenticazione.
- I ruoli hanno permessi distinti per consultazione, configurazione soglie e gestione anagrafiche/utenti.
- I permessi sono verificati lato server, non solo nascosti nell'interfaccia.

### 9. Stato operativo e istruzioni di avvio

Come responsabile voglio verificare salute dei componenti e dei dispositivi e avviare il sistema seguendo documentazione.

**Fatto quando:**

- È disponibile lo stato dei componenti principali e dell'ultimo contatto dei dispositivi.
- I log distinguono un problema sorgente da un problema applicativo.
- La documentazione descrive installazione, configurazione, avvio e verifica dello stato.

### 10. Spiegazione AI di un'anomalia (opzionale)

Come operatore voglio una breve spiegazione testuale di un andamento anomalo, per avere un aiuto interpretativo senza delegare le decisioni.

**Fatto quando:**

- La spiegazione usa dati disponibili ed è presentata come supporto, non come diagnosi certa.
- L'AI non modifica misure, soglie o anomalie e un suo errore non blocca la dashboard.
- L'integrazione, i dati trasmessi e le condizioni d'uso sono configurabili e documentati.
