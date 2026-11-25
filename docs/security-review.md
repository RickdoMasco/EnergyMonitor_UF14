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

| #   | Ingresso                     | Esposto a   | Stato                                   |
| --- | ---------------------------- | ----------- | --------------------------------------- |
| 1   | `POST /api/simulators`       | chiunque    | **Nessuna autenticazione**              |
| 2   | `GET /api/simulators`        | chiunque    | **Nessuna autenticazione**              |
| 3   | `POST /api/measurements`     | chiunque    | **Nessuna autenticazione**              |
| 4   | Ingress NGINX (`/`, `/api`)  | rete        | HTTP in chiaro, nessun TLS              |
| 5   | Dipendenze npm / Gradle      | —           | lockfile presente, nessun controllo CVE |
| 6   | PostgreSQL (porta 5433 host) | rete locale | credenziali da `config/dev.env`         |

## Checklist

| #   | Categoria                      | Domanda verificabile                                                                                                                                  | Risposta     | Evidenza                                                                                                                                                                                                                                                                    |
| --- | ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | Autenticazione                 | Chiamando un endpoint protetto senza token/sessione valida, ottengo un rifiuto (401) e non il dato?                                                   | **No**       | Nessun endpoint è protetto. Nessuna dipendenza Spring Security in `backend/build.gradle`; `BackendController` non ha annotazioni di sicurezza.                                                                                                                              |
| 2   | Autorizzazione per ruolo       | Chiamando un endpoint con un token valido ma di un ruolo che non dovrebbe poter eseguire l'operazione, ottengo 403 e non l'esecuzione?                | **No**       | Nessun `@PreAuthorize`, nessun `SecurityFilterChain`. I ruoli esistono solo come dati (branch `feature/implementazione-RBAC`), non come controlli.                                                                                                                          |
| 3   | Validazione input lato server  | Inviando all'API, senza passare dal form, un valore fuori dai valori ammessi o di tipo sbagliato, il server rifiuta con 400 e un messaggio specifico? | **Parziale** | `CreateMeasurement` ha `@NotNull`/`@NotBlank` e `@Valid` sul controller. `CreateSimulator` **non ha alcuna validazione**: `ipAddress` e `macAddress` accettano qualsiasi stringa.                                                                                           |
| 4   | Secret fuori dal codice        | Cercando `password`, `key`, `secret`, `token` nel codice e in `git log -p`, non trovo valori reali, solo riferimenti a variabili d'ambiente?          | **Sì**       | `.gitignore` esclude `.env`, `config/*.env`, `config.json`, `k8s/*.local.yaml`. `application.properties` legge da variabili d'ambiente. In history compare solo il segnaposto `POSTGRES_PASSWORD=cambiami` in `.env.example` (commit `a756562`), nessuna credenziale reale. |
| 5   | Dipendenze                     | Esiste un lockfile committato e uno strumento di controllo vulnerabilità è stato eseguito, con esito noto, prima di oggi?                             | **Parziale** | Lockfile presenti (`frontend/package-lock.json`, `package-lock.json`). **Nessun controllo CVE eseguito**: nessun `npm audit`, nessun `gradle dependencyCheck`.                                                                                                              |
| 6   | Logging sicuro                 | Aprendo i log di un'operazione recente trovo chi/cosa/quando/su cosa, senza password, token o dati personali non necessari?                           | **No**       | Nessun mascheramento dati sensibili su `develop`. Il mascheramento esiste solo sul branch `feature/18-validazione-ingestion` (PR #61), non ancora mergiato.                                                                                                                 |
| 7   | Tracciabilità delle operazioni | Per un'operazione sensibile (assegnazione, cambio stato, cambio ruolo) esiste una riga di log che permette di ricostruire chi l'ha fatta e quando?    | **No**       | Nessun audit log. Nessuna operazione sensibile tracciata con autore e data/ora.                                                                                                                                                                                             |

## Criticità trovate

| ID  | Categoria                | Descrizione                                                                     | Dove (file/endpoint)                                     | Corretta | Riferimento                                        |
| --- | ------------------------ | ------------------------------------------------------------------------------- | -------------------------------------------------------- | -------- | -------------------------------------------------- |
| C1  | Autenticazione           | Nessun framework di sicurezza: nessun endpoint richiede autenticazione          | `backend/build.gradle`, `api/BackendController.java`     | **No**   | —                                                  |
| C2  | Autorizzazione per ruolo | Nessun controllo di ruolo lato server                                           | `api/BackendController.java`                             | **No**   | —                                                  |
| C3  | Validazione input        | `CreateSimulator` accetta `ipAddress`/`macAddress` senza validazione di formato | `api/BackendRequests.java`                               | **No**   | Branch `fix/controllo-integrita-dati` (senza PR)   |
| C4  | Logging sicuro           | Nessun mascheramento di dati sensibili nei log                                  | `backend/src/main/resources/`                            | **No**   | Branch `feature/18-validazione-ingestion` (PR #61) |
| C5  | Tracciabilità            | Nessun audit log delle operazioni significative                                 | —                                                        | **No**   | —                                                  |
| C6  | Trasporto                | Ingress senza TLS: traffico HTTP in chiaro                                      | `k8s/ingress.yaml`                                       | **No**   | —                                                  |
| C7  | Dipendenze               | Nessun controllo CVE sulle dipendenze                                           | —                                                        | **No**   | —                                                  |
| C8  | Password                 | Password salvata in chiaro (nessun hashing)                                     | Branch `feature/implementazione-RBAC`, `UserEntity.java` | **No**   | —                                                  |

## Note per chi legge questa review

Tutte le criticità sono **aperte**: nessuna correzione è ancora nel codice su
`develop`. Due sono già implementate su branch non mergiati (C3, C4) e
diventeranno corrette al merge delle rispettive PR.

La review incrociata di un altro team è ancora da fare.
