# EnergyMonitor_UF14

|              |                                                     |
| ------------ | --------------------------------------------------- |
| **Cliente**  | _AlpEnergia Servizi S.p.A_                          |
| **Team**     | _TEnergy_                                           |
| **Membri**   | _Riccardo Mascotto -- "GitMaster e ProjectMenager"_ |
|              | _Alessandro Passerini -- "Frontend developer"_      |
|              | _Giacomo Grattarola -- "Backend developer"_         |
|              | _Emanuele Rossi -- "Backend developer e DevOps"_    |
|              | _Sebastiano Dalpez -- "Full Stack developer"_       |
| **Data**     | _30/09/2026_                                        |
| **Versione** | _v0.1_                                              |

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

## Avvio in sviluppo

Prerequisiti: JDK 21 e Node.js con npm. Per usare il database locale con Docker
serve anche Docker Desktop in modalità **Linux containers**. In alternativa
è possibile usare un'istanza PostgreSQL già disponibile, configurandone la
connessione.

1. Dalla root del repository, crea il file locale per lo sviluppo a partire
   dal template:

    PowerShell:

    ```powershell
    Copy-Item .env.example config\dev.env
    ```

    bash/zsh (Linux/macOS):

    ```bash
    cp .env.example config/dev.env
    ```

    Compila `config/dev.env` con i valori per il database locale. Con Docker
    puoi usare questa configurazione:

    ```env
    POSTGRES_HOST=localhost
    POSTGRES_PORT=5433
    POSTGRES_DB=energymonitor
    POSTGRES_USER=energymonitor
    POSTGRES_PASSWORD=INSERISCI_UNA_PASSWORD_LOCALE
    SPRING_PROFILES_ACTIVE=
    ```

    Sostituisci `INSERISCI_UNA_PASSWORD_LOCALE` con una password scelta da te,
    senza lasciare il testo segnaposto. Docker e backend devono usare gli
    stessi valori per database, utente e password:

    - `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` inizializzano
      PostgreSQL in Docker e sono usati dal backend per collegarsi.
    - `POSTGRES_PORT` è la porta pubblicata sul computer (`5433`), mappata
      sulla porta `5432` del container; il backend si collega a
      `localhost:5433`.
    - `POSTGRES_HOST` è usato dal backend; con il backend avviato sul
      computer, usa `localhost`.
    - `SPRING_PROFILES_ACTIVE` può restare vuota: senza un profilo diverso,
      il backend usa `dev` e carica `config/dev.env`.

    Se la porta `5433` è occupata, scegli una porta libera e impostala in
    `POSTGRES_PORT`; Compose e backend useranno entrambi quel valore. Le
    variabili `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` vengono
    applicate da PostgreSQL quando inizializza per la prima volta il volume:
    cambiarle in seguito non modifica automaticamente un database già
    inizializzato. Il file è ignorato da Git e non va condiviso o committato.
    Se usi invece un PostgreSQL già installato, imposta `POSTGRES_PORT` sulla
    porta di quell'istanza (spesso `5432`) e non avviare il servizio Docker.

2. Se usi il database Docker, avvialo dalla root del repository:

    ```powershell
    docker compose --env-file .\config\dev.env up -d --build
    ```

    ```bash
    docker compose --env-file ./config/dev.env up -d --build
    ```

    Attendi che PostgreSQL sia pronto prima di avviare il backend.

3. In un altro terminale, sempre dalla root del repository, avvia Spring Boot:

    ```powershell
    .\backend\gradlew.bat -p backend bootRun
    ```

    ```bash
    ./backend/gradlew -p backend bootRun
    ```

    L'applicazione usa il profilo `dev` se non ne viene specificato uno e legge
    le impostazioni da `config/dev.env`. Spring Boot/Hibernate crea e aggiorna
    le tabelle in base alle entità JPA (`spring.jpa.hibernate.ddl-auto=update`):
    non occorre eseguire uno script SQL.

4. Avviare il frontend:

    Dalla root del repository, crea `config.json` dal template e imposta nel
    campo `license` la chiave PrimeNG usata dal progetto. Il file contiene una
    configurazione locale e non va committato:

    ```powershell
    Copy-Item config.json.example config.json
    ```

    ```bash
    cp config.json.example config.json
    ```

    Poi, dalla cartella `frontend`, installa le dipendenze (la prima volta o
    quando cambiano) e avvia il server di sviluppo:

    ```powershell
    Set-Location frontend
    npm ci
    npm run start
    ```

    ```bash
    cd frontend
    npm ci
    npm run start
    ```

    `npm run start` genera `src/environments/environment.ts` a partire da
    `config.json`, quindi avvia Angular. Apri `http://localhost:4200/`; le
    chiamate `/api` vengono inoltrate al backend su `http://localhost:8080`,
    che deve essere già avviato. `config.json` e il file `environment.ts`
    generato sono esclusi da Git.

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
occupata, dalla root avvialo con
`.\backend\gradlew.bat -p backend bootRun --args='--server.port=18080'`.
Gli endpoint implementati per l'acquisizione misure sono descritti in
[`docs/API.md`](./docs/API.md). Per arrestare PostgreSQL, eseguire
`docker compose down`: i dati restano nel volume `postgres-data`. Il comando
`docker compose down -v` elimina definitivamente il volume e tutti i dati del
database. Per i dettagli sulla configurazione e sul backend, consulta
[`backend/README.md`](./backend/README.md) e
[`docs/configuration.md`](./docs/configuration.md).

## Formattazione

_La codebased usa la configurazione di formattazione reperibile da:_

```bash
npm install --save-dev @rickdomasco/prettier-config prettier
```

## Struttura del repository

```
.
├── .github/
│   └── copilot-instructions.md
├── .env.example           template per creare config/dev.env
├── .gitignore
├── README.md              avvio e panoramica del progetto
├── CONTRIBUTING.md        branch, commit e Definition of Done
├── package.json           configurazione Prettier condivisa
├── package-lock.json
├── config.json.example    template della configurazione locale frontend
├── config/
│   └── dev.env            configurazione locale, ignorata da Git
├── docker-compose.yml     database PostgreSQL per lo sviluppo
├── docker/
│   └── postgres/
│       └── Dockerfile
├── backend/               backend Java/Spring Boot (Gradle)
│   ├── README.md
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   └── src/
│       ├── main/
│       │   ├── java/it/energymonitor/backend/
│       │   │   ├── api/
│       │   │   ├── domain/
│       │   │   ├── persistence/
│       │   │   └── service/
│       │   └── resources/application.properties
│       └── test/
│           └── java/.../MeasurementApiTests.java
├── frontend/              applicazione Angular
│   ├── README.md
│   ├── package.json
│   ├── package-lock.json
│   ├── angular.json
│   ├── .prettierrc
│   ├── .postcssrc.json
│   ├── proxy.conf.json    inoltra /api al backend locale
│   ├── set-env.js         genera environment.ts dalla config root
│   ├── public/
│   └── src/
│       ├── app/
│       │   ├── home-page/
│       │   ├── view-active-sensors/
│       │   ├── interfaces/
│       │   └── app.routes.ts
│       ├── index.html
│       ├── main.ts
│       └── styles.css
└── docs/                  documentazione funzionale e tecnica
    ├── API.md
    ├── architecture-v1.md
    ├── backlog.md
    ├── configuration.md
    ├── data-model.md
    ├── domande-cliente.md
    ├── requirements.md
    └── use-cases.md
```

La struttura non mostra file generati o dipendenze installate come
`node_modules/`, `backend/build/` e `frontend/src/environments/`. `config/dev.env`
e `config.json` sono file locali ignorati da Git, creati seguendo i passaggi
di avvio.

## Stato del progetto

_Una breve descrizione su cosa è già stato fatto e cosa manca —Aggiornare a ogni lezione._

> "Lezione 1: inizio compilazione delle docs di sviluppo

> "Lezione 2: repository creato, readme completato, creazione issue, contributing completato

## Come contribuire

Branch strategy, convenzioni di commit e Definition of Done sono in
[`CONTRIBUTING.md`](./CONTRIBUTING.md) — leggetelo prima di aprire il primo branch.
