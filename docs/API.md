# API — Energy Monitor

|              |                                              |
| ------------ | -------------------------------------------- |
| **Progetto** | *Energy Monitor — AlpEnergia Servizi S.p.A.* |
| **Team**     | *TEnergy*                                    |
| **Data**     | *14/10/2026*                                 |
| **Versione** | *v0.1 — solo gli endpoint disponibili oggi* |

_Questa versione documenta solo gli endpoint realmente esistenti e funzionanti nel backend attuale. Un endpoint non riportato qui non è stato implementato e quindi non va considerato disponibile per integrazione._

## Convenzioni generali

Tutte le risposte sono JSON. Il percorso base è `/api` e tutti gli endpoint documentati qui sono esposti a partire da quel prefisso. Gli errori hanno sempre il formato `{ "errore": "descrizione leggibile" }` insieme allo status code corretto, senza `200 OK` con messaggi di errore nel body. Le date/ora sono in timestamp ISO-8601 UTC; i campi obbligatori devono essere presenti nel JSON della richiesta.

## Endpoint

### `POST /api/simulators`

Registra un nuovo simulatore di sorgente dati per consentire l’invio di misure.

**Corpo della richiesta**

```json
{
  "ipAddress": "192.168.1.10",
  "macAddress": "AA:BB:CC:DD:EE:FF"
}
```

**Risposte**

| Status | Quando | Corpo (esempio) |
|---|---|---|
| `201 Created` | il simulatore viene registrato correttamente | `{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }` |
| `400 Bad Request` | il body JSON non è valido o manca un dato richiesto | `{ "errore": "richiesta non valida o incompleta" }` |
| `415 Unsupported Media Type` | il `Content-Type` non è `application/json` | `{ "errore": "Content-Type deve essere application/json" }` |

### `GET /api/simulators`

Recupera l’elenco dei simulatori registrati.

**Risposte**

| Status | Quando | Corpo (esempio) |
|---|---|---|
| `200 OK` | la richiesta va a buon fine | `[{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }]` |

### `POST /api/measurements`

Acquisisce e persiste una misura inviata da un simulatore registrato.

**Corpo della richiesta**

```json
{
  "simulatorId": 1,
  "quantity": "temperatura",
  "value": 21.5,
  "unit": "°C",
  "measuredAt": "2026-10-14T09:00:00Z"
}
```

**Risposte**

| Status | Quando | Corpo (esempio) |
|---|---|---|
| `201 Created` | la misura è valida e viene salvata | `{ "id": 1, "simulatorId": 1, "quantity": "temperatura", "value": 21.5, "unit": "°C", "measuredAt": "2026-10-14T09:00:00Z" }` |
| `400 Bad Request` | il payload contiene un campo obbligatorio mancante o un valore non valido | `{ "errore": "quantity è obbligatorio" }` |
| `404 Not Found` | il simulatore indicato non esiste | `{ "errore": "simulatore non trovato: 99" }` |
| `415 Unsupported Media Type` | il `Content-Type` non è `application/json` | `{ "errore": "Content-Type deve essere application/json" }` |

## Test collegati

Attualmente non sono presenti test dedicati sotto una cartella `tests/` né sotto `backend/src/test`. I tre endpoint sopra sono quindi documentati come contratto attivo, ma non hanno ancora una copertura automatica verificabile nel repository.

- `POST /api/simulators` → nessun test presente al momento
- `GET /api/simulators` → nessun test presente al momento
- `POST /api/measurements` → nessun test presente al momento