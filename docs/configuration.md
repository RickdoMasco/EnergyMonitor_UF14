# Configurazione ambienti

Questa applicazione legge la configurazione da file `.env` o da `config/*.env` in base all'ambiente attivo.

## Principio di base

Il backend Spring Boot importa automaticamente i file di configurazione in questo ordine:

1. `config/<profile>.env` dove `<profile>` è il valore di `SPRING_PROFILES_ACTIVE`
2. fallback legacy: `.env` nella root del repository

Il file di configurazione necessario per il database è legato alle variabili PostgreSQL:

- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

L'importazione è definita in [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties).

## Ambiente di sviluppo (dev)

Per il locale usare `config/dev.env`.

```env
POSTGRES_HOST=
POSTGRES_PORT=
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=
```

Default consigliati:

- `POSTGRES_HOST=localhost`
- `POSTGRES_PORT=5432`
- `POSTGRES_DB=energymonitor`
- `POSTGRES_USER=energymonitor`
- `POSTGRES_PASSWORD=cambiami`
- `SERVER_PORT=8080`

Esempio di avvio locale:

PowerShell:

```powershell
Set-Location backend
$env:SPRING_PROFILES_ACTIVE = "dev"
.\gradlew.bat bootRun
```

bash/zsh:

```bash
cd backend
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

Per avviare il database con Docker, si usa il file di ambiente dedicato:

PowerShell:

```powershell
docker compose --env-file .\config\dev.env up -d --build
```

bash/zsh:

```bash
docker compose --env-file ./config/dev.env up -d --build
```

## Ambiente di test (test)

Per il profilo di test usare `config/test.env`.

```env
POSTGRES_HOST=
POSTGRES_PORT=
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
SPRING_PROFILES_ACTIVE=test
SERVER_PORT=
```

Default consigliati:

- `POSTGRES_HOST=localhost`
- `POSTGRES_PORT=5432`
- `POSTGRES_DB=energymonitor_test`
- `POSTGRES_USER=energymonitor`
- `POSTGRES_PASSWORD=cambiami`
- `SERVER_PORT=8081`

Questo ambiente è utile quando il backend viene eseguito in un contesto di validazione o deployment di test, e lo si distingue dal database di sviluppo.

Esempio di esecuzione con profilo di test:

PowerShell:

```powershell
Set-Location backend
$env:SPRING_PROFILES_ACTIVE = "test"
.\gradlew.bat bootRun
```

bash/zsh:

```bash
cd backend
export SPRING_PROFILES_ACTIVE=test
./gradlew bootRun
```

## Regola pratica

- `dev.env` è per lo sviluppo locale
- `test.env` è per l'ambiente di test / manifest di deployment
- se non è impostato `SPRING_PROFILES_ACTIVE`, il backend usa di default `dev`

## File creati

I file reali da usare sono in `config/`:

- `config/dev.env`
- `config/test.env`

Sono esclusi da commit tramite `.gitignore` e vanno creati localmente sulla base di questa documentazione.
