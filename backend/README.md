# Backend

Il backend è un'applicazione Java 21 con Spring Boot e Gradle. Il flusso
principale è organizzato in controller API (`api`), logica applicativa
(`service`), dominio (`domain`) e persistenza JPA (`persistence`).

## Avvio in sviluppo

I comandi seguenti partono dalla root del repository. Prima di avviare il
backend, crea `config/dev.env` copiando il template `.env.example` e valorizza
le variabili PostgreSQL. Per lo sviluppo locale senza un PostgreSQL già
installato, avvia il database Docker:

```powershell
docker compose --env-file .\config\dev.env up -d --build
```

Poi avvia Spring Boot in un altro terminale:

```powershell
.\backend\gradlew.bat -p backend bootRun
```

Il profilo predefinito è `dev`; l'applicazione legge `config/dev.env` e
ascolta su `http://localhost:8080`. Per i comandi bash/zsh e i dettagli della
configurazione, consulta [`docs/configuration.md`](../docs/configuration.md).

Hibernate crea e aggiorna lo schema PostgreSQL in avvio; non occorre eseguire
migrazioni SQL manuali.

## Test

```powershell
.\backend\gradlew.bat -p backend test
```

I test usano H2 in memoria e non richiedono un database PostgreSQL o Docker.

## Configurazione locale

`config/dev.env` e gli altri file `config/*.env` sono locali, ignorati da Git
e possono contenere credenziali: non committarli. Il backend conserva anche
il supporto legacy per `.env` alla root, ma il file in `config/` è il percorso
raccomandato. `docker compose` non carica automaticamente i file di ambiente
del backend: passagli esplicitamente `--env-file .\config\dev.env`.
