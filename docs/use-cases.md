# Attori e casi d'uso — Energy Monitor / AlpEnergia Servizi S.p.A.

|              |                                                                                                         |
| ------------ | ------------------------------------------------------------------------------------------------------- |
| **Team**     | TEnergy: Alessandro Passerini, Riccardo Mascotto, Giacomo Grattarola, Emanuele Rossi, Sebastiano Dalpez |
| **Cliente**  | AlpEnergia Servizi S.p.A.                                                                               |
| **Data**     | 30/09/2026                                                                                              |
| **Versione** | v1                                                                                                      |

## Attori

_Un attore è un ruolo, non una persona: la stessa persona può essere due
attori. Prendeteli dalla sezione "utenti e ruoli" della richiesta cliente._

| Attore                     | Cosa ottiene dal sistema                                                       |
| -------------------------- | ------------------------------------------------------------------------------ |
| Operatore energetico       | Consulta misure, trend, dashboard e anomalie; registra la presa visione.       |
| Responsabile               | Consulta indicatori aggregati e configura/verifica soglie.                     |
| Amministratore             | Gestisce edifici, dispositivi, utenti e configurazioni principali.             |
| Simulatore / sorgente dati | Invia misure tramite l'interfaccia di acquisizione; non accede alla dashboard. |
| Servizio AI (opzionale)    | Produce una spiegazione su dati derivati e non modifica dati o regole.         |

## Casi d'uso

_Ogni caso d'uso descrive un risultato osservabile; i dettagli ancora da
confermare sono elencati in `domande-cliente.md`._

### UC1 — Acquisire misure da più dispositivi

- **Attore**: Simulatore / sorgente dati.
- **Precondizione**: Dispositivi registrati e sorgente configurata con accesso all'interfaccia di acquisizione.
- **Risultato osservabile**: Le misure valide sono registrate con dispositivo, grandezza, valore, unità e data/ora; gli errori sono attribuiti alla sorgente interessata.

### Elenco endpoint

- `POST /api/simulators` — registra una nuova sorgente/simulatore dati. È il primo passaggio per configurare un dispositivo o un flusso di acquisizione.
- `GET /api/simulators` — recupera le sorgenti/simulatori configurati per verificare la disponibilità e lo stato delle fonti dati.
- `POST /api/measurements` — acquisisce una nuova misura da un simulatore/dispositivo. Il payload include i dati necessari per persistire valore, grandezza, unità e timestamp.

### UC2 — Consultare misure recenti e storico

- **Attore**: Operatore energetico.
- **Precondizione**: Utente autenticato e autorizzato; esistono misure per il filtro selezionato.
- **Risultato osservabile**: L'operatore vede valori recenti o una serie filtrata per edificio, dispositivo, grandezza e periodo.

#### Elenco endpoint

- `GET /api/v1/measurements?buildingId=&deviceId=&type=&from=&to=` — consulta lo storico delle misure con filtri per edificio, dispositivo, grandezza e intervallo temporale.
- `GET /api/v1/devices/{deviceId}/measurements/latest` — restituisce le ultime misure disponibili per un dispositivo specifico.

### UC3 — Consultare dashboard aggregata

- **Attore**: Operatore energetico o responsabile.
- **Precondizione**: Utente autenticato e almeno un edificio o dispositivo registrato.
- **Risultato osservabile**: La dashboard mostra lo stato sintetico dell'ambito, i dati recenti e le anomalie pertinenti.

#### Elenco endpoint

- `GET /api/v1/dashboard?buildingId=&groupId=&from=&to=` — restituisce gli indicatori aggregati del contesto richiesto, compresi i dati recenti e le anomalie rilevanti.

### UC4 — Configurare una soglia

- **Attore**: Responsabile.
- **Precondizione**: Utente autenticato con permesso di configurazione; grandezza e ambito sono configurati.
- **Risultato osservabile**: La soglia è persistita e la modifica è tracciata con autore e data/ora.

#### Elenco endpoint

- `PUT /api/v1/thresholds/{thresholdId}` — crea o aggiorna una soglia e registra il relativo audit di modifica.

### UC5 — Generare e consultare un'anomalia

- **Attore**: Sistema di elaborazione; consultazione da parte dell'operatore.
- **Precondizione**: È acquisita una misura valida pertinente a una soglia attiva.
- **Risultato osservabile**: Il sistema crea un'anomalia collegata a misura e soglia; l'operatore ne consulta valore, contesto e stato.

#### Elenco endpoint

- `GET /api/v1/anomalies?buildingId=&deviceId=&status=` — consulta le anomalie generate, filtrate per contesto e stato.

### UC6 — Registrare la presa visione

- **Attore**: Operatore energetico.
- **Precondizione**: Utente autenticato e anomalia consultabile.
- **Risultato osservabile**: La presa visione è associata all'utente e a data/ora; misura ed evento originario restano invariati.

#### Elenco endpoint

- `POST /api/v1/anomalies/{anomalyId}/acknowledgement` — registra la presa visione dell'operatore sull'anomalia selezionata.

### UC7 — Gestire edifici, dispositivi e utenti

- **Attore**: Amministratore.
- **Precondizione**: Utente autenticato con ruolo amministrativo.
- **Risultato osservabile**: Le informazioni sono create o aggiornate, persistono dopo il riavvio e le modifiche principali sono tracciate.

#### Elenco endpoint

- `POST /api/v1/buildings` — registra un nuovo edificio.
- `POST /api/v1/devices` — registra un nuovo dispositivo e lo associa a un edificio.

### UC8 — Verificare stato di sorgenti e componenti

- **Attore**: Amministratore o responsabile.
- **Precondizione**: Componenti applicativi avviati e raccolta dello stato abilitata.
- **Risultato osservabile**: Si distingue la salute della piattaforma da quella delle singole sorgenti, incluso l'ultimo contatto noto.

#### Elenco endpoint

- `GET /api/v1/health` — restituisce lo stato dei componenti applicativi e delle sorgenti di dati.

### UC9 — Ottenere una spiegazione AI (opzionale)

- **Attore**: Operatore energetico; servizio AI come attore secondario.
- **Precondizione**: Esiste un'anomalia con misure contestuali; la funzione AI è configurata e autorizzata.
- **Risultato osservabile**: L'operatore riceve una breve spiegazione di supporto, non vincolante; misure, soglie ed eventi non vengono modificati.

#### Elenco endpoint

- Endpoint da definire in fase di design API: un'operazione dedicata all'anomalia, ad esempio `POST /api/v1/anomalies/{anomalyId}/explanation` o equivalente, per generare una spiegazione contestuale senza alterare i dati originari.

### Flusso dati principale

1. L'amministratore registra edifici e dispositivi e configura il simulatore.
2. Il simulatore invia misure all'interfaccia di acquisizione.
3. Il sistema valida e registra le misure, valuta le soglie attive e genera eventuali anomalie.
4. Le API di consultazione forniscono misure e anomalie alla dashboard.
5. Se una sorgente è indisponibile, il problema viene registrato per quella sorgente; i dati già persistiti restano consultabili.
