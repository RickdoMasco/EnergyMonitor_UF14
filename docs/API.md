# API — Energy Monitor

|              |                                              |
| ------------ | -------------------------------------------- |
| **Progetto** | _Energy Monitor — AlpEnergia Servizi S.p.A._ |
| **Team**     | _TEnergy_                                    |
| **Data**     | _14/10/2026_                                 |
| **Versione** | _v0.1 — acquisizione misure (UC1)_           |

_Questa API v0.1 espone solo la registrazione dei dispositivi e l'acquisizione
di misure; edificio e associazione edificio-dispositivo non sono ancora gestiti
da queste route. Il contratto implementato conserva i nomi tecnici
`/api/simulators` e `simulatorId`; nel modello di dominio e nella proposta
architetturale v1 l'elemento è chiamato **dispositivo** e il suo identificativo
è `deviceId`. Le route REST proposte per v1 sono in
[architecture-v1.md](./architecture-v1.md) e non sono ancora implementate.
I dati API v0.1 sono persistiti in PostgreSQL._

## Vocabolario e passaggio alla proposta v1

Il nome di dominio unico è **dispositivo**; **grandezza** indica la categoria
della misura (per esempio temperatura o potenza). I campi `simulatorId` e
`quantity` e le route `/api/simulators` sono nomi tecnici del contratto v0.1
già implementato: `simulatorId` identifica il dispositivo e `quantity`
trasporta la grandezza. Non introducono entità di dominio separate chiamate
“simulatore” o “quantity”.

| Operazione | API v0.1 implementata | Proposta architettura v1 | Differenza |
| --- | --- | --- | --- |
| Registrare/elencare dispositivi | `POST /api/simulators` e `GET /api/simulators` | `POST /api/v1/devices` e `GET /api/v1/devices` | La v0.1 conserva il nome legacy `simulators` e restituisce `id`; la v1 usa il nome di dominio `devices` e l'identificativo `deviceId`. L'associazione con gli edifici è prevista solo nella v1. |
| Inviare misure | `POST /api/measurements` con `simulatorId` e una misura | `POST /api/v1/measurements` con `deviceId` e un batch | La v1 usa l'identificativo del dispositivo e raggruppa più misure in una richiesta. |
| Grandezza misurata | `quantity` | `quantity` | Stesso termine e significato; in italiano è “grandezza”. |

Le route di consultazione filtrata, edifici, dashboard, anomalie e soglie
descritte in [architecture-v1.md](./architecture-v1.md) sono proposte future,
non endpoint presenti nell'API v0.1.

## Convenzioni generali

Tutte le risposte sono JSON e il percorso base è `/api`. Gli errori hanno la
forma `{ "errore": "descrizione leggibile" }` con uno status non-successo.
Le date sono timestamp ISO-8601; una misura senza `measuredAt` usa l'ora
corrente. La grandezza viene creata al primo invio e riutilizzata senza
differenza tra maiuscole e minuscole.

## Persistenza e schema

Spring Boot/Hibernate crea e aggiorna le tabelle PostgreSQL in base alle
entità JPA all'avvio (`spring.jpa.hibernate.ddl-auto=update`). Non occorre
applicare manualmente uno script SQL; il database PostgreSQL deve essere
disponibile e configurato prima di avviare il backend.

## Endpoint

### `POST /api/simulators`

Registra un dispositivo che può inviare misure. La rotta mantiene il nome
legacy `/simulators` per compatibilità con il contratto v0.1; la route
proposta per la v1 è `POST /api/v1/devices`.

**Corpo della richiesta**

```json
{ "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF" }
```

Entrambi i campi sono facoltativi.

**Risposte**

| Status                       | Quando                     | Corpo (esempio)                                                                                    |
| ---------------------------- | -------------------------- | -------------------------------------------------------------------------------------------------- |
| `201 Created`                | dispositivo registrato      | `{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }` |
| `400 Bad Request`            | corpo JSON non valido      | `{ "errore": "richiesta non valida o incompleta" }`                                                |
| `415 Unsupported Media Type` | il Content-Type non è JSON | `{ "errore": "Content-Type deve essere application/json" }`                                        |

### `GET /api/simulators`

Elenca i dispositivi registrati. La route proposta per la v1 è
`GET /api/v1/devices`.

**Risposte**

| Status   | Quando                           | Corpo (esempio)                                                                                      |
| -------- | -------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `200 OK` | elenco disponibile (anche vuoto) | `[{ "id": 1, "ipAddress": "192.168.1.10", "macAddress": "AA:BB:CC:DD:EE:FF", "functioning": true }]` |

### `POST /api/measurements`

Acquisisce e persiste una misura inviata per un dispositivo registrato.
Il campo v0.1 `simulatorId` identifica il dispositivo; nella proposta v1 si
chiama `deviceId`.

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
| `404 Not Found`              | ID dispositivo inesistente                                     | `{ "errore": "simulatore non trovato: 99" }`                                                                                  |
| `415 Unsupported Media Type` | il Content-Type non è JSON                                       | `{ "errore": "Content-Type deve essere application/json" }`                                                                   |

## Test collegati

> `POST /api/simulators`, `GET /api/simulators` e `POST /api/measurements` →
> [MeasurementApiTests.java](../backend/src/test/java/it/energymonitor/backend/MeasurementApiTests.java):
> acquisizione da più dispositivi, riuso della grandezza, dispositivo inesistente,
> richiesta non valida e JSON malformato.

Il testo di errore `simulatore non trovato` è mantenuto perché fa parte del
comportamento implementato dall'API v0.1; non indica un'entità distinta nel
modello di dominio.
