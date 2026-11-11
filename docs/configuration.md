# Configurazione degli ambienti

## File di ambiente

Il file [`.env.example`](../.env.example) è un template: copialo in
`config/dev.env` e sostituisci i valori vuoti. Il file effettivo è ignorato da
Git (`config/*.env`), perché può contenere password e altre impostazioni
locali. Non committare né condividere i file `.env`.

PowerShell:

```powershell
Copy-Item .env.example config\dev.env
```

bash/zsh:

```bash
cp .env.example config/dev.env
```

Variabili PostgreSQL:

| Variabile           | Uso                                                                                            |
| ------------------- | ---------------------------------------------------------------------------------------------- |
| `POSTGRES_HOST`     | Host raggiungibile dal backend, normalmente `localhost`.                                       |
| `POSTGRES_PORT`     | Porta PostgreSQL sull'host |
| `POSTGRES_DB`       | Nome del database; Compose lo usa anche per inizializzare PostgreSQL.                          |
| `POSTGRES_USER`     | Utente PostgreSQL usato dal backend e da Compose.                                              |
| `POSTGRES_PASSWORD` | Password PostgreSQL usata dal backend e da Compose.                                            |

`SPRING_PROFILES_ACTIVE` seleziona il profilo Spring e il file
`config/<profilo>.env`. In assenza della variabile Spring usa `dev`, perciò
per lo sviluppo ordinario non è necessario impostarla. Se la si imposta
all'esterno, per esempio a `test`, Spring cerca `config/test.env`. Il valore
`SPRING_PROFILES_ACTIVE` contenuto in un file `.env` non seleziona il file da
caricare: la selezione avviene prima che quel file venga importato.

## Avvio di sviluppo

Con Docker Compose, dalla root del repository:

```powershell
docker compose --env-file .\config\dev.env up -d --build
```

Avvia quindi il backend, sempre dalla root:

```powershell
.\backend\gradlew.bat -p backend bootRun
```

Gli equivalenti bash/zsh sono:

```bash
docker compose --env-file ./config/dev.env up -d --build
./backend/gradlew -p backend bootRun
```

Compose riceve esplicitamente `config/dev.env` tramite `--env-file`; Spring
Boot importa il file secondo la configurazione in
[`application.properties`](../backend/src/main/resources/application.properties).
Le tabelle vengono create/aggiornate all'avvio da Hibernate. Se si usa un
PostgreSQL già installato invece di Compose, configura in `config/dev.env`
host, porta, database, utente e password per quell'istanza.

## File legacy `.env`

Il backend mantiene il supporto retrocompatibile a `.env` nella root del
repository (e ai percorsi equivalenti relativi alla directory di avvio).
Questa configurazione legacy è importata dopo i file `config/<profilo>.env` e
può quindi prevalere sui valori omonimi. Per evitare impostazioni inattese,
usa `config/dev.env` come unica configurazione locale; rimuovi o aggiorna
eventuali vecchi `.env` quando passi al nuovo schema.

Il template alla root non è un `.env` attivo e Docker Compose non lo carica da
solo: Compose usa il file indicato con `--env-file` (in questa guida,
`config/dev.env`).

## Test

I test automatici si eseguono dalla root:

```powershell
.\backend\gradlew.bat -p backend test
```

```bash
./backend/gradlew -p backend test
```

I test configurano H2 in memoria e non hanno bisogno di `config/test.env`,
Docker o PostgreSQL. Un file `config/test.env` serve solo se si avvia
esplicitamente l'applicazione con il profilo `test` contro un database
PostgreSQL dedicato; non va usato per i test automatici, per non rischiare di
collegarsi al database di sviluppo.
