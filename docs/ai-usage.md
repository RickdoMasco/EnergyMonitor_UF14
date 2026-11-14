# Uso dell'AI nel progetto

|              |                                                                                                  |
| ------------ | ------------------------------------------------------------------------------------------------ |
| **Progetto** | _EnergyMonitor_UF14_                                                                              |
| **Cliente**  | _AlpEnergia Servizi S.p.A._                                                                       |
| **Team**     | _TEnergy: Riccardo Mascotto, Alessandro Passerini, Giacomo Grattarola, Emanuele Rossi, Sebastiano Dalpez_ |
| **Data**     | _04/11/2026_                                                                                      |
| **Versione** | _v1 — dashboard frontend e testing ad hoc_                                                        |

Questo file registra gli usi significativi dell'AI che hanno prodotto codice,
test o decisioni entrati nel progetto. Per ogni caso sono riportati il
risultato ottenuto, la verifica eseguita e l'esito della revisione.

## Casi registrati

### Caso 1 — Dashboard di sintesi per edificio e gruppo dispositivi

- **Dove**: `frontend/src/app/dashboard/`, `frontend/src/app/interfaces/dashboard.interface.ts`,
  `frontend/src/app/app.routes.ts` e `frontend/src/app/home-page/home-page.html`.

- **Chiesto**: "Realizza una dashboard di sintesi per edificio o gruppo
  dispositivi. La vista deve mostrare indicatori recenti e anomalie, usare
  unicamente dati provenienti dalle API backend e comunicare assenza di dati,
  dati obsoleti, caricamento ed errore di connessione."

- **Ottenuto**: componenti Angular standalone per selezione dell'ambito,
  KPI, trend e anomalie; interfacce TypeScript tipizzate; servizio API con le
  chiamate a `GET /api/dashboard/scopes` e
  `GET /api/dashboard?scopeType=...&scopeId=...`; route `/dashboard` e
  collegamento dalla home. Sono stati previsti stati espliciti per loading,
  no-data, errore e dati stale, senza dati simulati hardcoded nel frontend.

- **Verificato**: eseguito
  `cd frontend && npx ng build --configuration development`.
  La compilazione Angular e il controllo dei template sono terminati con
  esito positivo. È stata inoltre letta la documentazione API e il controller
  backend: la versione v0.1 implementa solo dispositivi e misure, non ancora
  gli endpoint dashboard/building/group. La UI mantiene quindi l'errore API
  esplicito quando il backend non espone ancora queste route, senza introdurre
  un fallback fittizio.

- **Esito**: corretto e tenuto.

- **Perché**: la verifica del codice backend e di `docs/API.md` ha mostrato
  che gli endpoint v1 necessari alla dashboard sono ancora una proposta.

### Caso 2 — Testing ad hoc del frontend dashboard

- **Dove**: `frontend/src/app/dashboard/dashboard.spec.ts`.

- **Chiesto**: "Realizza il testing ad hoc per il frontend; non toccare il
  codice già scritto per far passare i test."

- **Ottenuto**: una suite Vitest/Angular con mock del servizio API, composta da
  sei test per caricamento ambiti, selezione edificio, rendering di KPI/trend/
  anomalie, stato stale, stato vuoto, errore nel caricamento degli ambiti,
  errore nel caricamento della dashboard e cambio da edificio a gruppo.

- **Verificato**: eseguito
  `cd frontend && npx ng test --watch=false --include='src/app/dashboard/dashboard.spec.ts'`.
  Risultato: `1` file di test passato e `6` test passati. È stata eseguita
  anche la suite completa con `npx ng test --watch=false`: il nuovo test
  continua a passare.

- **Esito**: tenuto integralmente; i test preesistenti non sono stati modificati.

- **Perché**: i test specifici verificano i comportamenti richiesti dalla
  feature e passano senza modifiche al codice della dashboard.


## Log dei test generati

| Test | Tipo | Verificato come | Esito | Perché |
|---|---|---|---|---|
| Caricamento ambiti e selezione del primo edificio | happy path | Mock di `getScopes()` con edificio e gruppo; verificata chiamata dashboard con `building` e id `7` | tenuto | Copre la selezione iniziale dell'ambito |
| Rendering KPI, trend, anomalie e stale | happy path | Verificato il testo renderizzato e il numero di card KPI/anomalie nel DOM | tenuto | Copre gli indicatori richiesti dalla feature |
| Nessun ambito disponibile | negativo | Mock di `getScopes()` con array vuoto; verificata assenza della chiamata dashboard e messaggio no-data | tenuto | Evita una selezione implicita non valida |
| Errore nel caricamento degli ambiti | negativo | Mock API con `throwError`; verificati messaggio di errore e stato `error` | tenuto | Rende visibile il mancato collegamento al backend |
| Errore nel caricamento della dashboard | negativo | Mock di `getDashboard()` con `throwError`; verificato azzeramento dei dati e messaggio di errore | tenuto | Evita di mostrare dati precedenti come se fossero aggiornati |
| Cambio da edificio a gruppo | comportamento | Cambiato `selectedScopeType` a `group`; verificati id selezionato e parametri API | tenuto | Copre il requisito edificio/gruppo |

## Feature sviluppata con l'AI — revisione del team

- **Feature**: dashboard frontend di sintesi per edificio o gruppo dispositivi,
  con KPI, trend, anomalie e stati di caricamento/no-data/errore/stale.

- **Letta riga per riga da**: Alessandro Passerini.

- **Revisionata da**: Sebastiano Dalpez
