# Security review — Energy Monitor

|                          |                                            |
| ------------------------ | ------------------------------------------ |
| **Progetto / cliente**   | Energy Monitor (AlpEnergia Servizi S.p.A.) |
| **Team compilatore**     | TEnergy                                    |
| **Progetto revisionato** | Energy Monitor (autovalutazione)           |
| **Data**                 | 25/11/2026                                 |
| **Versione**             | 1.0                                        |

> Compilata come **autovalutazione** sul branch `develop`. La review incrociata
> di un altro team non è ancora stata fatta.

## Superficie d'attacco

Punti di ingresso non fidati dell'applicazione:

| #   | Ingresso                     | Esposto a   | Stato                                                          |
| --- | ---------------------------- | ----------- | -------------------------------------------------------------- |
| 1   | `POST /api/simulators`       | chiunque    | **Nessuna autenticazione**                                     |
| 2   | `GET /api/simulators`        | chiunque    | **Nessuna autenticazione**                                     |
| 3   | `POST /api/measurements`     | chiunque    | **Nessuna autenticazione**                                     |
| 4   | Ingress NGINX (`/`, `/api`)  | rete        | HTTP in chiaro, nessun TLS                                     |
| 5   | Dipendenze npm / Gradle      | —           | lockfile presente, `npm audit` eseguito, Gradle non verificato |
| 6   | PostgreSQL (porta 5433 host) | rete locale | credenziali da `config/dev.env`                                |

## Checklist

| #   | Categoria                      | Domanda verificabile                                                                                                                                            | Risposta     | Evidenza                                                                                                                                                                                                                                                                                                                                                                                                |
| --- | ------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Autenticazione                 | Chiamando un endpoint protetto senza token/sessione valida, ottengo un rifiuto (401) e non il dato?                                                             | **No**       | Nessun endpoint è protetto. Nessuna dipendenza Spring Security in `backend/build.gradle`; `BackendController` non ha annotazioni di sicurezza.                                                                                                                                                                                                                                                          |
| 2   | Autorizzazione per ruolo       | Chiamando un endpoint con un token valido ma di un ruolo che non dovrebbe poter eseguire l'operazione, ottengo 403 e non l'esecuzione?                          | **No**       | Nessun `@PreAuthorize`, nessun `SecurityFilterChain`. I ruoli esistono solo come dati (branch `feature/implementazione-RBAC`), non come controlli.                                                                                                                                                                                                                                                      |
| 3   | Validazione input lato server  | Inviando all'API, senza passare dal form, un valore fuori dai valori ammessi o di tipo sbagliato, il server rifiuta con un errore 4xx e un messaggio specifico? | **Sì**       | `@Valid` sul controller per entrambe le richieste. `CreateMeasurement` (`@NotNull`/`@NotBlank`/`@Size`) → `400` con i messaggi dei campi. `CreateSimulator` (`@NotBlank` + `@Pattern` IPv4/MAC in `api/BackendRequests.java`) → `422 Unprocessable Content` con la mappa `campi` (`ApiExceptionHandler`). JSON non leggibile → `400`. Coperto da `MeasurementApiTests`.                                 |
| 4   | Secret fuori dal codice        | Cercando `password`, `key`, `secret`, `token` nel codice e in `git log -p`, non trovo valori reali, solo riferimenti a variabili d'ambiente?                    | **Sì**       | `.gitignore` esclude `.env`, `config/*.env`, `config.json`, `k8s/*.local.yaml`. `application.properties` legge da variabili d'ambiente. In history compare solo il segnaposto `POSTGRES_PASSWORD=cambiami` in `.env.example` (commit `a756562`), nessuna credenziale reale.                                                                                                                             |
| 5   | Dipendenze                     | Esiste un lockfile committato e uno strumento di controllo vulnerabilità è stato eseguito, con esito noto, prima di oggi?                                       | **Parziale** | Lockfile presenti (`frontend/package-lock.json`, `package-lock.json`). `npm audit` eseguito sul frontend il 26/11/2026: `zod` < 4.6.6 (high, nel bundle di produzione) corretto in #71; `npm audit --omit=dev` → 0 vulnerabilità. Resta `vite` 8.3.0 in `@angular/build` (high, solo build/sviluppo). **Backend non verificato**: nessun `gradle dependencyCheck`.                                      |
| 6   | Logging sicuro                 | Aprendo i log di un'operazione recente trovo chi/cosa/quando/su cosa, senza password, token o dati personali non necessari?                                     | **Sì**       | `logback-spring.xml` applica `SensitiveDataMaskingConverter` (messaggi) e `SensitiveThrowableMaskingConverter` (stack trace): i valori di `password`, `token`, `secret`, `apiKey`, `Authorization` diventano `***`. `RequestOutcomeLogger` registra esito, status, metodo, percorso, `simulatorId` e IP senza corpo della richiesta. Test: `SensitiveDataMaskingConverterTests`, `MeasurementApiTests`. |
| 7   | Tracciabilità delle operazioni | Per un'operazione sensibile (assegnazione, cambio stato, cambio ruolo) esiste una riga di log che permette di ricostruire chi l'ha fatta e quando?              | **No**       | Nessun audit log. Nessuna operazione sensibile tracciata con autore e data/ora.                                                                                                                                                                                                                                                                                                                         |

## Criticità trovate

| ID  | Categoria                | Descrizione                                                                     | Dove (file/endpoint)                                     | Corretta     | Riferimento                                                                                                                                                     |
| --- | ------------------------ | ------------------------------------------------------------------------------- | -------------------------------------------------------- | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| C1  | Autenticazione           | Nessun framework di sicurezza: nessun endpoint richiede autenticazione          | `backend/build.gradle`, `api/BackendController.java`     | **No**       | —                                                                                                                                                               |
| C2  | Autorizzazione per ruolo | Nessun controllo di ruolo lato server                                           | `api/BackendController.java`                             | **No**       | —                                                                                                                                                               |
| C3  | Validazione input        | `CreateSimulator` accetta `ipAddress`/`macAddress` senza validazione di formato | `api/BackendRequests.java`                               | **Sì**       | `@NotBlank` + `@Pattern` su `CreateSimulator`, `422` con `campi` in `api/ApiExceptionHandler.java` (PR #64)                                                     |
| C4  | Logging sicuro           | Nessun mascheramento di dati sensibili nei log                                  | `backend/src/main/resources/`                            | **Sì**       | `logback-spring.xml`, `logging/SensitiveDataMaskingConverter.java`, `logging/SensitiveThrowableMaskingConverter.java` (PR #61)                                  |
| C5  | Tracciabilità            | Nessun audit log delle operazioni significative                                 | —                                                        | **No**       | —                                                                                                                                                               |
| C6  | Trasporto                | Ingress senza TLS: traffico HTTP in chiaro                                      | `k8s/ingress.yaml`                                       | **No**       | —                                                                                                                                                               |
| C7  | Dipendenze               | Nessun controllo CVE sulle dipendenze                                           | `frontend/package-lock.json`, `backend/build.gradle`     | **Parziale** | Frontend: `npm audit` eseguito, `zod` corretto (#71); `vite` solo dev accettato per questo rilascio (serve Angular 22.3). Backend: controllo CVE ancora da fare |
| C8  | Password                 | Password salvata in chiaro (nessun hashing)                                     | Branch `feature/implementazione-RBAC`, `UserEntity.java` | **No**       | —                                                                                                                                                               |

## Note per chi legge questa review

C3 e C4 sono **risolte** nel codice su `develop` (validazione di
`CreateSimulator` e mascheramento dei dati sensibili nei log). C7 è
**parziale**: le dipendenze di produzione del frontend non hanno vulnerabilità
note (#71), mentre restano `vite` (solo build/sviluppo, rischio accettato per
questo rilascio) e il controllo CVE del backend. Le altre criticità (C1, C2,
C5, C6, C8) restano **aperte**.

La review incrociata di un altro team è ancora da fare.
