# CONTRIBUTING

|              |                                                                                                           |
| ------------ | --------------------------------------------------------------------------------------------------------- |
| **Progetto** | _EnergyMonitor_UF14_                                                                                      |
| **Team**     | _TEnergy, Alessandro Passerini, Emanuele Rossi, Giacomo Grattarola, Riccardo Mascotto, Sebastiano Dalpez_ |
| **Data**     | _30/09/2026_                                                                                              |
| **Versione** | _v0.1_                                                                                                    |

Queste regole valgono per **tutti** i membri del team, comprese le persone
che le hanno scritte. Se una regola non viene rispettata da nessuno, cambiarla
qui — non ignorarla in silenzio.

## Branch strategy

```
main                 sempre funzionante, nessun commit diretto, rappresenta la produzione
develop              branch di sviluppo, nessun commit diretto, accoglie le feature completate ma non deve necessariamente essere production ready
feature/<cosa>       nuova funzionalità
fix/<cosa>           correzione di un bug
docs/<cosa>          solo documentazione
```

> Esempio: `feature/filtro-interventi`, `fix/calcolo-priorita`, `docs/api-storage`.

_Un branch vive il tempo di un'attività: nasce da `develop`, si sviluppa, si
integra con una PR/MR, e si chiude. Non deve durare settimane.

Il branch `develop` nasce da `main`, si integra con una PR/MR quando stabile e production ready,
vive un tempo indefinito o fino a termine del progetto.

Il branch `fix` puo' nascere da `main` per hotfix e deve essere integrato su `main` e `develop` tramite PR/MR._

## Convenzioni di commit

```
feat: <cosa aggiunge>
fix: <cosa corregge>
docs: <cosa documenta>
wip: <su cosa si sta lavorando>
```

> Esempio: `feat: aggiungi filtro interventi per tecnico`

## Pull/merge request e review

- Chi può fare merge su `main`? _Solamente il gitmaster Riccardo Mascotto_
- Chi può fare merge su `develop`? _Chiunque nel team, dopo approvazione_
- Quante approvazioni servono prima del merge? _1, da chi non ha
  scritto il codice_
- Cosa NON è accettabile in una review? _approvare senza aver letto il
  diff_

## Gestione dei conflitti

_Regola minima: cosa si fa se un conflitto non si risolve in fretta._

> Se un conflitto non si risolve in 10 minuti, si chiama un altro
> membro del team prima di forzare una scelta da soli.

## Definition of Done

_Riprendete la Definition of Done concordata alla lezione 1 (se già scritta):
non è decorativa, è il criterio con cui si accetta o si rifiuta una PR._

> Una issue è fatta quando: il codice è mergiato su `develop` · esiste
> un modo di verificarla (test o passi manuali) · la issue collegata è
> aggiornata a 'fatto' · nessun segreto o dato finto è rimasto nel codice."

- **Integrazione:** Il codice è stato revisionato, approvato e mergiato con successo sul branch **`develop`** (o `main` in caso di hotfix).
- **Tracciabilità:** La Issue collegata sulla board è stata aggiornata allo stato "Fatto".
- **Sicurezza e Pulizia:** Nessun segreto (chiavi API, password), dato finto di debug o magic value è rimasto all'interno del codice sorgente.

## Issue e board

_Dove vivono le issue (piattaforma indicata dal docente) e gli stati che
usate — devono coincidere con quelli mostrati nella board del team._

```
da fare → in corso → in revisione → fatto
```
