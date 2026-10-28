# EnergyMonitor_UF14

|              |                                   |
| ------------ | --------------------------------- |
| **Cliente**  | _AlpEnergia Servizi S.p.A_        |
| **Team**     | _TEnergy_                         |
| **Membri**   | _Riccardo Mascotto -- "Ruolo"_    |
|              | _Alessandro Passerini -- "Ruolo"_ |
|              | _Giacomo Grattarola -- "Ruolo"_   |
|              | _Emanuele Rossi -- "Ruolo"_       |
|              | _Sebastiano Dalpez -- "Ruolo"_    |
| **Data**     | _30/09/2026_                      |
| **Versione** | _v0.1_                            |

## Cosa fa questo progetto

`docs/requirements.md`

Il cliente analizza i dati di consumo di vari apparecchi elettrici, insieme a dati di contorno riguardanti l'ambiente dove sono contenuti questi apparecchi.

Attualemente questo processo di analisi dei dati è manuale, quindi:

Il cliente desidera una piattaforma unica capace di ricevere dati da sorgenti differenti. Questi dati devono essere conservati e mostrati in modo chiaro ed organizzato (in una dashboard) in modo che l'utente finale riesca ad avere una visione precisa dell'andamento dei consumi nei vari ambienti e riesca a notare eventuali situazioni anomale.

Quindi, il software richiesto deve semplificare ed "automatizzare" (anche tramite A.I.) la lettura e l'analisi dei dati.

## Stack

```
Backend: Java 21, Spring Boot
Frontend: Angular
Database: PostgreSQL 17
```

Spring Boot è stato scelto per costruire le API REST con un framework Java
consolidato e per integrare in modo diretto dependency injection, accesso ai
dati e persistenza JPA. PostgreSQL è il database relazionale del progetto:
vincoli, relazioni e transazioni aiutano a mantenere coerenti dispositivi,
grandezze e misure, mentre le query permettono di consultare lo storico per
dispositivo e intervallo temporale. Le motivazioni e i confini di queste scelte
sono descritti in [`docs/architecture-v1.md`](./docs/architecture-v1.md).

## Come si esegue

Prerequisiti per l'esecuzione locale: Docker Desktop in modalità **Linux containers** e JDK 21.

1. Creare la configurazione locale e impostare una password:

   PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

   bash/zsh (Linux/macOS):

   ```bash
   cp .env.example .env
   ```

   In `.env`, `POSTGRES_PORT=5433` evita conflitti con un PostgreSQL locale
   sulla porta predefinita `5432`.

2. Avviare PostgreSQL:

   PowerShell:

   ```powershell
   docker compose --env-file .\.env up -d --build
   ```

   bash/zsh:

   ```bash
   docker compose --env-file ./.env up -d --build
   ```

3. Avviare il backend da un altro terminale:

   PowerShell:

   ```powershell
   Set-Location backend
   .\gradlew.bat bootRun
   ```

   bash/zsh:

   ```bash
   cd backend
   ./gradlew bootRun
   ```

   All'avvio, Spring Boot/Hibernate crea e aggiorna le tabelle in base alle
   entità JPA (`spring.jpa.hibernate.ddl-auto=update`); non occorre eseguire
   uno script SQL.

### Test

Dalla root del repository eseguire:

PowerShell:

```powershell
.\backend\gradlew.bat -p backend test
```

bash/zsh:

```bash
./backend/gradlew -p backend test
```

I test di integrazione usano H2 in memoria e uno schema dedicato ai test; non
richiedono Docker o PostgreSQL e non modificano il database di sviluppo.

Il backend è disponibile su `http://localhost:8080`; se la porta è già
occupata, avviarlo con `.\gradlew.bat bootRun --args='--server.port=18080'`.
Gli endpoint implementati per l'acquisizione misure sono descritti in
[`docs/API.md`](./docs/API.md). Per arrestare PostgreSQL, eseguire
`docker compose down`: i dati restano nel volume `postgres-data`. Il comando
`docker compose down -v` elimina definitivamente il volume e tutti i dati del
database. Per una spiegazione semplice dei file del backend, consulta
[`backend/README.md`](./backend/README.md).

## Formattazione

_La codebased usa la configurazione di formattazione reperibile da:_

```bash
npm install --save-dev @rickdomasco/prettier-config prettier
```

## Struttura del repository

```
.
├── README.md            questo file
├── CONTRIBUTING.md       come contribuire: branch, commit, Definition of Done
├── .env.example          variabili di configurazione locali (copiare in .env)
├── docker-compose.yml    servizi locali (database PostgreSQL)
├── docker/
│   └── postgres/
│       └── Dockerfile    immagine PostgreSQL del progetto
├── backend/              backend Spring Boot (Gradle)
└── docs/
    ├── requirements.md   requisiti (lezione 1)
    ├── use-cases.md      casi d'uso (lezione 1)
    ├── architecture-v1.md architettura (lezione 1)
    └── data-model.md     modello dei dati
```

## Stato del progetto

_Una breve descrizione su cosa è già stato fatto e cosa manca —Aggiornare a ogni lezione._

> "Lezione 1: inizio compilazione delle docs di sviluppo
> "Lezione 2: repository creato, readme completato, creazione issue, contributing completato

## Come contribuire

Branch strategy, convenzioni di commit e Definition of Done sono in
[`CONTRIBUTING.md`](./CONTRIBUTING.md) — leggetelo prima di aprire il primo branch.
