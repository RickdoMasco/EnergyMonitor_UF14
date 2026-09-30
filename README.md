# Energy Monitor

|              |                                                                 |
| ------------ | --------------------------------------------------------------- |
| **Cliente**  | _Nome del cliente simulato — es. "NordFacility S.r.l."_         |
| **Team**     | _Nome/numero team, es. "Team 3"_                                |
| **Membri**   | _Un nome per riga, con il ruolo se già definito_                |
| **Data**     | _Data di questo commit del README_                              |
| **Versione** | _v0.1 — aumenta quando il contenuto cambia in modo sostanziale_ |

## Cosa fa questo progetto


`docs/requirements.md`

Il cliente analizza i dati di consumo di vari apparecchi elettrici, insieme a dati di contorno riguardanti l'ambiente dove sono contenuti questi apparecchi. 

Attualemente questo processo di analisi dei dati è manuale, quindi:  

Il cliente desidera una piattaforma unica capace di ricevere dati da sorgenti differenti. Questi dati devono essere conservati e mostrati in modo chiaro ed organizzato (in una dashboard) in modo che l'utente finale riesca ad avere una visione precisa dell'andamento dei consumi nei vari ambienti e riesca a notare eventuali situazioni anomale.

Quindi, il software richiesto deve semplificare ed "automatizzare" (anche tramite A.I.) la lettura e l'analisi dei dati.   

## Stack

_Dichiarate qui, in una riga per componente, cosa avete scelto — non è
imposto dal corso, ma va detto esplicitamente perché chi clona il repository
sappia cosa aspettarsi._

```
Stack: <linguaggio/framework backend>
Frontend: <framework o "nessuno, per ora">
Database: <tecnologia scelta>
```

> Esempio: `Stack: Node.js + Express` · `Frontend: React` · `Database: PostgreSQL`

## Come si esegue

_Anche solo pochi comandi indicativi, aggiornateli quando l'applicazione
esiste davvero — oggi può bastare "non ancora eseguibile, in costruzione"._

```
<comando o comandi per avviare il progetto in locale, quando esisteranno>
```

## Struttura del repository

```
.
├── README.md            questo file
├── CONTRIBUTING.md       come contribuire: branch, commit, Definition of Done
└── docs/
    ├── requirements.md   requisiti (lezione 1)
    ├── use-cases.md      casi d'uso (lezione 1)
    └── architecture-v1.md architettura (lezione 1)
```

_Aggiornate l'albero quando aggiungete cartelle vere (es. `src/`, `tests/`):
questo file deve restare uno specchio fedele di cosa c'è nel repository._

## Stato del progetto

_Una riga onesta su cosa è già fatto e cosa manca — non un elenco di feature
desiderate. Aggiornatela a ogni lezione._

> Esempio: "Lezione 2: repository creato, branch strategy concordata, backlog
> trasferito in issue. Non esiste ancora codice applicativo."

## Come contribuire

Branch strategy, convenzioni di commit e Definition of Done sono in
[`CONTRIBUTING.md`](./CONTRIBUTING.md) — leggetelo prima di aprire il primo branch.
