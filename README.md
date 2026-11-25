# EnergyMonitor_UF14

|              |                                                     |
| ------------ | --------------------------------------------------- |
| **Cliente**  | _AlpEnergia Servizi S.p.A_                          |
| **Team**     | _TEnergy_                                           |
| **Membri**   | _Riccardo Mascotto -- "GitMaster e ProjectMenager"_ |
|              | _Alessandro Passerini -- "Full Stack developer"_    |
|              | _Giacomo Grattarola -- "Backend developer"_         |
|              | _Emanuele Rossi -- "Backend developer e DevOps"_    |
|              | _Sebastiano Dalpez -- "Full Stack developer"_       |
| **Data**     | _30/09/2026_                                        |
| **Versione** | _v0.1_                                              |

## Cosa fa questo progetto

Il cliente analizza i dati di consumo di vari apparecchi elettrici, insieme a dati di contorno riguardanti l'ambiente dove sono contenuti questi apparecchi.

Attualemente questo processo di analisi dei dati è manuale, quindi:

Il cliente desidera una piattaforma unica capace di ricevere dati da sorgenti differenti. Questi dati devono essere conservati e mostrati in modo chiaro ed organizzato (in una dashboard) in modo che l'utente finale riesca ad avere una visione precisa dell'andamento dei consumi nei vari ambienti e riesca a notare eventuali situazioni anomale.

Quindi, il software richiesto deve semplificare ed "automatizzare" (anche tramite A.I.) la lettura e l'analisi dei dati.

Requisiti completi in [docs/requirements.md](./docs/requirements.md).

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

## Avvio

Prerequisiti: JDK 21, Node.js con npm. Per il database locale con Docker serve
Docker Desktop in modalità **Linux containers**; in alternativa va bene
un'istanza PostgreSQL già disponibile.

Ogni ambiente ha la sua guida. Parti da qui e apri quella che ti serve:

| Ambiente            | Guida                                                                                 | Cosa avvia                                                    |
| ------------------- | ------------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| Sviluppo locale     | [backend/README.md](./backend/README.md) + [frontend/README.md](./frontend/README.md) | Backend su `:8080`, frontend su `:4200`, PostgreSQL in Docker |
| Kubernetes          | [K8S_README.md](./K8S_README.md)                                                      | Backend e frontend nel cluster, PostgreSQL in Docker          |
| Configurazione      | [docs/configuration.md](./docs/configuration.md)                                      | Variabili d'ambiente, file `config/*.env`, profili Spring     |
| Rilascio e rollback | [docs/deployment.md](./docs/deployment.md)                                            | Procedura di rilascio, checklist, rollback                    |

**Prima di tutto**, crea i file di configurazione locali (ignorati da Git):

```powershell
Copy-Item .env.example config\dev.env
Copy-Item config.json.example config.json
```

Poi compila `config/dev.env` con i valori PostgreSQL e `config.json` con la
chiave PrimeNG. I dettagli delle variabili sono in
[docs/configuration.md](./docs/configuration.md).

### Test

| Componente | Comando                                 | Note                                   |
| ---------- | --------------------------------------- | -------------------------------------- |
| Backend    | `.\backend\gradlew.bat -p backend test` | H2 in memoria, nessun Docker richiesto |
| Frontend   | `npm run test` (da `frontend/`)         | Vitest via Angular CLI                 |

Il backend risponde su `http://localhost:8080`; se la porta è occupata,
avvialo con `.\backend\gradlew.bat -p backend bootRun --args='--server.port=18080'`.
Gli endpoint implementati sono in [`docs/API.md`](./docs/API.md). Per fermare
PostgreSQL usa `docker compose down` (i dati restano nel volume
`postgres-data`); `docker compose down -v` elimina anche il volume.

## Formattazione

Il repository usa Prettier e la configurazione condivisa dichiarata nel
`package.json` root. Dalla root del repository, controlla o formatta i file
Markdown:

```bash
npm exec -- prettier --check "**/*.md"
npm exec -- prettier --write "**/*.md"
```

## Struttura del repository

```
.
├── .env.example           template per creare config/dev.env
├── .gitignore
├── .github/
│   ├── ISSUE_TEMPLATE/    template di issue (bug, feature)
│   └── PULL_REQUEST_TEMPLATE.md
├── README.md              avvio e panoramica del progetto
├── CONTRIBUTING.md        branch, commit e Definition of Done
├── K8S_README.md          avvio e diagnostica Kubernetes
├── package.json           configurazione Prettier condivisa
├── package-lock.json
├── config.json.example    template della configurazione locale frontend
├── config/
│   ├── dev.env            configurazione locale, ignorata da Git
│   └── config.json        configurazione locale per la build Docker frontend, ignorata da Git
├── docker-compose.yml     database PostgreSQL per lo sviluppo
├── docker/
│   ├── backend/
│   │   └── Dockerfile
│   ├── frontend/
│   │   ├── Dockerfile
│   │   └── nginx.conf
│   └── postgres/
│       └── Dockerfile
├── k8s/                   manifest Kubernetes e template Secret
│   ├── configmap.yaml
│   ├── deployment-backend.yaml
│   ├── deployment-frontend.yaml
│   ├── ingress.yaml
│   ├── secret-example.yaml
│   ├── service-backend.yaml
│   └── service-frontend.yaml
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
│       │   ├── app.routes.ts
│       │   ├── app.config.ts
│       │   ├── app.ts
│       │   ├── app.html
│       │   ├── app.css
│       │   ├── app.spec.ts
│       │   ├── home-page/
│       │   │   ├── home-page.ts
│       │   │   ├── home-page.html
│       │   │   └── home-page.css
│       │   ├── view-active-sensors/
│       │   │   ├── view-active-sensors.ts
│       │   │   ├── view-active-sensors.html
│       │   │   └── view-active-sensors.css
│       │   ├── dashboard/
│       │   │   ├── dashboard.ts
│       │   │   ├── dashboard.html
│       │   │   ├── dashboard.css
│       │   │   ├── dashboard-api.service.ts
│       │   │   └── dashboard.spec.ts
│       │   └── interfaces/
│       │       ├── dashboard.interface.ts
│       │       └── sensor.interface.ts
│       ├── index.html
│       ├── main.ts
│       └── styles.css
└── docs/                  documentazione funzionale e tecnica
    ├── API.md
    ├── ai-usage.md
    ├── architecture-v1.md
    ├── backlog.md
    ├── configuration.md
    ├── data-model.md
    ├── deployment.md
    ├── domande-cliente.md
    ├── requirements.md
    ├── security-review.md
    ├── test-plan.md
    └── use-cases.md
```

La struttura riporta i file principali e omette file generati o dipendenze
installate come `node_modules/`, `backend/build/` e
`frontend/src/environments/`. `config/dev.env`, `config.json` e
`config/config.json` sono file locali ignorati da Git, creati seguendo i
passaggi di avvio; `config/config.json` serve per la build dell'immagine
frontend.

## Documentazione

| Documento                                            | Contenuto                                       |
| ---------------------------------------------------- | ----------------------------------------------- |
| [docs/requirements.md](./docs/requirements.md)       | Requisiti funzionali e non funzionali           |
| [docs/use-cases.md](./docs/use-cases.md)             | Attori e casi d'uso                             |
| [docs/architecture-v1.md](./docs/architecture-v1.md) | Componenti, responsabilità, scelte tecnologiche |
| [docs/data-model.md](./docs/data-model.md)           | Entità, attributi, relazioni, schema tabelle    |
| [docs/API.md](./docs/API.md)                         | Contratto delle API implementate                |
| [docs/configuration.md](./docs/configuration.md)     | Variabili d'ambiente e profili                  |
| [docs/deployment.md](./docs/deployment.md)           | Rilascio, checklist, rollback                   |
| [docs/security-review.md](./docs/security-review.md) | Checklist di sicurezza e criticità              |
| [docs/test-plan.md](./docs/test-plan.md)             | Matrice di test dei requisiti                   |
| [docs/ai-usage.md](./docs/ai-usage.md)               | Uso tracciato dell'AI                           |
| [docs/backlog.md](./docs/backlog.md)                 | Voci di backlog ordinate                        |
| [docs/domande-cliente.md](./docs/domande-cliente.md) | Ambiguità da chiarire con il cliente            |

## Stato del progetto

Il backend implementa l'API v0.1 per registrare/elencare simulatori e
acquisire misure (`POST/GET /api/simulators`, `POST /api/measurements`).
L'interfaccia Angular include le route home, sensori attivi e dashboard; le
API per dashboard, edifici e gruppi sono ancora proposte e non implementate
nel backend. Il modello utenti/ruoli e l'autenticazione BCrypt sono
documentazione di progetto, non funzionalità backend già disponibili. Per
stato e dettagli dei contratti, consulta [docs/API.md](./docs/API.md),
[docs/architecture-v1.md](./docs/architecture-v1.md) e
[docs/data-model.md](./docs/data-model.md).

## Come contribuire

Branch strategy, convenzioni di commit e Definition of Done sono in
[`CONTRIBUTING.md`](./CONTRIBUTING.md) — leggetelo prima di aprire il primo branch.
