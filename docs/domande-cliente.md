# Domande al cliente — Energy Monitor / AlpEnergia Servizi S.p.A

| | |
|---|---|
| **Team** | *TEnergy, Alessandro Passerini, Riccardo Mascotto, Giacomo Grattarola, Emanuele Rossi, Sebastiano Dalpez* |
| **Cliente** | *AlpEnergia Servizi S.p.A* |
| **Data** | 23/09/2026 |

## Domande aperte

| # | Domanda | Perché è ambigua | Impatto se cambia la risposta |
|---|---|---|---|
| 1 | Qual è la frequenza prevista per la ricezione dei "dati periodici" (es. ogni secondo, 15 minuti, giornaliera)? | Il documento richiede la "ricezione di dati simulati" in modo periodico, ma omette l'intervallo temporale. | Determina il volume di carico sul sistema: una frequenza elevata impone l'uso di code di messaggistica e database time-series, mentre intervalli lunghi permettono un approccio relazionale più standard. |
| 2 | La "presa visione delle anomalie" prevede un workflow di gestione (es. possibilità di marcare l'evento come "risolto", "in lavorazione" o "falso positivo")? | È richiesta la "consultazione e presa visione", ma non è chiaro se le anomalie debbano cambiare stato dopo l'intervento dell'operatore o rimanere un semplice log testuale. | Richiede la progettazione di una macchina a stati per le anomalie e l'implementazione di API specifiche per l'aggiornamento, non solo per la lettura. |
| 3 | La generazione della "spiegazione testuale" tramite AI avviene in automatico per ogni anomalia registrata o viene attivata on-demand dall'operatore? | Si richiede un "supporto AI" per spiegare gli andamenti anomali, ma non si specifica in quale momento del flusso l'LLM debba essere interrogato. | Attivazioni automatiche ad ogni superamento soglia aumenterebbero drasticamente i costi delle API esterne e i tempi di elaborazione dell'evento rispetto a una generazione su richiesta. |
| 4 | Come deve essere dedotta l'indisponibilità di un dispositivo (es. mancanza di invio dati per un tempo X, oppure tramite ping attivi dalla piattaforma)? | Si richiede di "distinguere un dispositivo non raggiungibile", ma i sensori simulati sembrano funzionare in logica push (invio verso il sistema), non pull. | Definisce se implementare un meccanismo passivo (controllo dei timeout sui timestamp di ultimo invio) oppure attivo (servizio di health-check che interroga i sensori). |
| 5 | Qual è il periodo di conservazione (retention) richiesto per lo storico dei dati? I dati vecchi vanno mantenuti intatti, aggregati (rollup) o cancellati? | Viene richiesta la "visualizzazione [...] dello storico" e la dimostrazione di "trend temporali", ma manca un limite temporale ai dati da mantenere caldi a database. | Impatta pesantemente il dimensionamento dello storage, i costi infrastrutturali e la necessità di creare job asincroni per l'aggregazione dei dati storici. |
| 6 | Le soglie per la generazione di eventi sono fisse per l'intero sistema, configurabili per singolo edificio o personalizzabili per ogni singolo dispositivo? | Si chiede la "configurazione di soglie per almeno alcune grandezze", ma non è specificato il livello di granularità di questa configurazione. | Modifica profondamente la struttura relazionale del database e la logica del motore di validazione (rule engine) che valuta i dati in ingresso. |
| 7 | Cosa deve contenere il tracciamento delle modifiche alle soglie e per quanto va conservato (chi, quando, valore precedente/nuovo, retention del log di audit)? | Il documento richiede il "tracciamento delle modifiche alle soglie e alle configurazioni principali", ma non indica quali informazioni registrare né il periodo di conservazione. | Determina lo schema della tabella di audit (o l'uso di un log append-only), la possibilità di ricostruire lo storico delle configurazioni e il dimensionamento dello storage dedicato ai log. |
| 8 | Quale meccanismo di autenticazione si intende adottare (credenziali locali, SSO aziendale, MFA)? | Si richiede "accesso riservato agli utenti autorizzati" e "protezione delle credenziali", ma non viene specificato il metodo di autenticazione né se esiste un'identità aziendale con cui integrarsi. | Cambia l'implementazione del modulo di sicurezza (gestione password/hash, integrazione con provider esterni OIDC/SAML, MFA) e l'intero flusso di login e gestione sessioni. |
| 9 | I dati di consumo e le anomalie possono essere inviati a un provider AI esterno per generare la spiegazione testuale, o esistono vincoli su cosa può uscire dalla piattaforma? | Il documento chiede un "supporto AI" per spiegare gli andamenti anomali, ma non chiarisce se i dati possano essere trasmessi a servizi di terze parti né se vi siano dati sensibili da tutelare. | Decide se usare un LLM cloud (con obbligo di anonimizzazione/mascheramento dei dati) oppure un modello self-hosted, con impatti su costi, privacy/compliance e architettura del servizio AI. |

| # | Decisione presa (se non ancora chiarita dal cliente) | Perché |
|---|---|---|
| 1 | In attesa di chiarimento | |
| 2 | In attesa di chiarimento | |
| 3 | In attesa di chiarimento | |
| 4 | In attesa di chiarimento | |
| 5 | In attesa di chiarimento | |
| 6 | In attesa di chiarimento | |
| 7 | In attesa di chiarimento | |
| 8 | In attesa di chiarimento | |
| 9 | In attesa di chiarimento | |
