# CONTRIBUTING

|              |                                                                                                           |
| ------------ | --------------------------------------------------------------------------------------------------------- |
| **Progetto** | _EnergyMonitor_UF14_                                                                                      |
| **Team**     | _TEnergy: Alessandro Passerini, Emanuele Rossi, Giacomo Grattarola, Riccardo Mascotto, Sebastiano Dalpez_ |
| **Data**     | _06/10/2026_                                                                                              |
| **Versione** | _v0.1_                                                                                                    |

Queste regole valgono per **tutti** i membri del team, comprese le persone
che le hanno scritte. Se una regola non viene rispettata da nessuno, cambiarla
qui — non ignorarla in silenzio.

## Branch strategy

```
main                 sempre funzionante, nessun commit diretto, rappresenta la produzione
develop              branch di sviluppo, nessun commit diretto, accoglie le feature completate ma non deve necessariamente
                     essere production ready
feature/<cosa>       nuova funzionalità
fix/<cosa>           correzione di un bug
docs/<cosa>          solo documentazione
```

> Esempio: `feature/filtro-interventi`, `fix/calcolo-priorita`, `docs/api-storage`.

Un branch vive il tempo di un'attività: nasce da `develop`, si sviluppa, si
integra con una PR/MR, e si chiude.

Il branch `develop` nasce da `main`, si integra con una PR/MR quando stabile e production ready,
vive un tempo indefinito o fino a termine del progetto.

Il branch `fix` puo' nascere da `main` per hotfix e deve essere integrato su `main` e `develop` tramite PR/MR.

## Convenzioni di commit

```
feat: <cosa aggiunge>
fix: <cosa corregge>
docs: <cosa documenta>
wip: <su cosa si sta lavorando>
```

> Esempio: `feat: aggiunto filtro interventi per tecnico`

## Pull/merge request e review

Per garantire la qualità del codice, il processo di sviluppo e revisione segue regole rigorose:

- **Apertura in Draft:** Non appena si comincia a lavorare su una issue, aprire la Pull Request relativa in stato di "Draft"
  su GitHub. Questo serve a dare visibilità al team su cosa si sta lavorando ed evitare duplicati.
- **Uso dei Template:** Ogni Issue e Pull Request deve essere creata utilizzando il template ufficiale del repository.
- **Prerequisito per la Review:** Prima di togliere lo stato di Draft e richiedere la review visiva, lo sviluppatore deve
  assicurarsi che il codice sia formattato localmente con Prettier e di non aver tralasciato magic values o codice commentato.

Per evitare dubbi:

- Chi può fare merge su `main`? _Solamente il gitmaster **Riccardo Mascotto**_
- Chi può fare merge su `develop`? _Chiunque nel team, dopo approvazione da parte di un altro membro_
- Quante approvazioni servono prima del merge? _1, da chi non ha scritto il codice_
- Cosa NON è accettabile in una review? _Approvare senza aver letto il diff, approvare senza avere verificato che il codice
  soddisfi i prerequisiti per la review_

## Gestione dei conflitti

- Se un conflitto non si risolve in 10 minuti, si chiama un altro membro del team prima di forzare una scelta da soli.
- Se il conflitto coinvolge porzione di codice scritto da altri membri, analizzare staticamene il codice ed eventualmente
  confrontarsi con essi per risolvere il conflitto

## Definition of Done

Una issue è considerabile completata quando: il codice è mergiato su `develop` · esiste un modo di verificarla
(test o passi manuali) · la issue collegata è aggiornata a 'fatto' · nessun segreto o dato finto è rimasto nel codice.

Per contrassengnare le issue come completate solamente quando il codice prodotto rispecchia e risolve il `Fatto quando:` del
file backlog.md e/o i requisiti indicati all'interno della descrizione della issue stessa.

Il codice prodotto deve rispettare i seguenti principi:

- **Integrazione:** Il codice è stato revisionato, approvato e mergiato con successo sul branch **`develop`**
  (o `main` in caso di hotfix).
- **Tracciabilità:** La Issue collegata sulla board è stata aggiornata allo stato "Fatto".
- **Sicurezza e Pulizia:** Nessun segreto (chiavi API, password), dato finto di debug, codice commentato o magic value
  deve rimanere all'interno del codice sorgente.

## Issue e board

Le issue di questo progetto sono gestite direttamente su **GitHub**.
Il team utilizza una bacheca Kanban per tracciare lo stato di avanzamento delle attività.

Il flusso di lavoro segue rigorosamente i seguenti stati:

- **da fare:** Contiene i task approvati, nuove funzionalità da sviluppare o i bug non ancora presi in carico.
- **in corso:** Lo sviluppatore apre una Pull Request in modalità **Draft** e lavora al codice.
- **in revisione:** Lo sviluppatore toglie lo stato di Draft dalla Pull Request e richiede la code review a un membro del team.
- **fatto:** Le modifiche sono state approvate, il codice è stato mergiato nel ramo principale e il task è concluso.

**Link alla board del team:** [link alla kanban]
