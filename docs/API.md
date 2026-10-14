# API — Energy Monitor

|              |                                              |
| ------------ | -------------------------------------------- |
| **Progetto** | _Energy Monitor — AlpEnergia Servizi S.p.A._ |
| **Team**     | _TEnergy_                                    |
| **Data**     | _14/10/2026_                                 |
| **Versione** | _v0.1 — acquisizione misure (UC1)_           |

_Questa versione contiene solo gli endpoint per registrare simulatori e
acquisire misure. Le misure e i simulatori sono persistiti in PostgreSQL._

## Convenzioni generali

Tutte le risposte sono JSON e il percorso base è `/api`. Gli errori hanno la
forma `{ "errore": "descrizione leggibile" }` con uno status non-successo.
Le date sono timestamp ISO-8601; una misura senza `measuredAt` usa l'ora
corrente. La grandezza viene creata al primo invio e riutilizzata senza
differenza tra maiuscole e minuscole.

## Creazione schema PostgreSQL

Spring valida lo schema all'avvio, ma non crea né modifica tabelle.
Da PowerShell, nella root del repository e con PostgreSQL avviato:

```powershell
Get-Content backend\src\main\resources\db\schema.sql -Raw |
  docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
```

Lo script crea solo le tabelle usate da UC1 ed è ripetibile.

## Endpoint

### `POST /api/simulators`

Registra un simulatore di sorgente per poter inviare misurazioni.

**Corpo della richiesta**

```json
{ "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF" }
```

Entrambi i campi sono facoltativi.

**Risposte**

| Status                       | Quando                     | Corpo (esempio)                                                                                    |
| ---------------------------- | -------------------------- | -------------------------------------------------------------------------------------------------- |
| `201 Created`                | simulatore registrato      | `{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }` |
| `400 Bad Request`            | corpo JSON non valido      | `{ "errore": "richiesta non valida o incompleta" }`                                                |
| `415 Unsupported Media Type` | il Content-Type non è JSON | `{ "errore": "Content-Type deve essere application/json" }`                                        |

### `GET /api/simulators`

Elenca i simulatori registrati.

**Risposte**

| Status   | Quando                           | Corpo (esempio)                                                                                      |
| -------- | -------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `200 OK` | elenco disponibile (anche vuoto) | `[{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }]` |

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

`measuredAt` è facoltativo.

**Risposte**

| Status                       | Quando                                                           | Corpo (esempio)                                                                                                               |
| ---------------------------- | ---------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `201 Created`                | misura valida registrata                                         | `{ "id": 1, "simulatorId": 1, "quantity": "temperatura", "value": 21.5, "unit": "°C", "measuredAt": "2026-10-14T09:00:00Z" }` |
| `400 Bad Request`            | campo obbligatorio mancante, valore non finito o JSON non valido | `{ "errore": "quantity è obbligatorio" }`                                                                                     |
| `404 Not Found`              | simulatore inesistente                                           | `{ "errore": "simulatore non trovato: 99" }`                                                                                  |
| `415 Unsupported Media Type` | il Content-Type non è JSON                                       | `{ "errore": "Content-Type deve essere application/json" }`                                                                   |

## Test collegati

> `POST /api/simulators`, `GET /api/simulators` e `POST /api/measurements` →
> [MeasurementApiTests.java](../backend/src/test/java/it/energymonitor/backend/MeasurementApiTests.java):
> acquisizione da più simulatori, riuso della grandezza, simulatore inesistente,
> richiesta non valida e JSON malformato.
