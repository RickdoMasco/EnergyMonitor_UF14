# Requisiti — Energy Monitor / AlpEnergia Servizi S.p.A.

|              |                                                                                                         |
| ------------ | ------------------------------------------------------------------------------------------------------- |
| **Team**     | TEnergy: Alessandro Passerini, Riccardo Mascotto, Giacomo Grattarola, Emanuele Rossi, Sebastiano Dalpez |
| **Cliente**  | AlpEnergia Servizi S.p.A.                                                                               |
| **Data**     | 30/09/2026                                                                                              |
| **Versione** | v1                                                                                                      |

## Problema

AlpEnergia riceve dati di consumo da sistemi differenti e li analizza
manualmente. Gli operatori non dispongono di una vista unificata e tempestiva
dei consumi di edifici e dispositivi. Individuare superamenti di soglia e
andamenti anomali richiede attività manuali e non offre una base scalabile per
l'aumento dei dispositivi.

## Obiettivi del progetto

Raccogliere misure periodiche da più dispositivi; consultare valori recenti,
storico e trend per edificio e dispositivo; rilevare superamenti di soglia;
offrire una vista sintetica dello stato; predisporre una base modulare per
future integrazioni con sistemi di telelettura reali.

## Requisiti funzionali

_Requisiti ricavati dalla richiesta. Frequenze, formule e comportamenti non
specificati restano soggetti alle domande in `domande-cliente.md`._

| #    | Requisito                                                                                                                                     |
| ---- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| RF1  | L'amministratore registra, modifica e disattiva edifici e dispositivi, associando ogni dispositivo a un edificio e alle grandezze supportate. |
| RF2  | Il sistema acquisisce misure simulate da più dispositivi e registra dispositivo, grandezza, valore, unità di misura e data/ora.               |
| RF3  | Il sistema valida le misure in ingresso e segnala payload incompleti, valori non interpretabili o dispositivi sconosciuti.                    |
| RF4  | L'operatore consulta ultimo valore e storico filtrando per edificio, dispositivo, grandezza e intervallo temporale.                           |
| RF5  | Il responsabile crea e modifica soglie per una grandezza e un ambito configurato; la semantica è da confermare.                               |
| RF6  | Una misura valida che supera una soglia attiva genera un'anomalia collegata alla misura e alla soglia.                                        |
| RF7  | L'operatore consulta le anomalie e registra la presa visione senza modificare la misura originale.                                            |
| RF8  | La dashboard mostra una sintesi per edificio o gruppo di dispositivi, con dati recenti e anomalie pertinenti.                                 |
| RF9  | Simulatore e dashboard accedono ai dati tramite interfacce definite, indipendenti tra loro.                                                   |
| RF10 | Il sistema registra gli esiti di ricezione/elaborazione e rende consultabili stato dei componenti e ultimo contatto delle sorgenti.           |
| RF11 | Il sistema distingue i permessi di operatore, responsabile e amministratore lato server.                                                      |
| RF12 | Le modifiche a soglie e configurazioni principali sono tracciate con autore, data/ora e variazione.                                           |
| RF13 | Se abilitata, l'AI produce una breve spiegazione di un'anomalia usando dati disponibili, senza modificare misure, soglie o eventi.            |

## Requisiti non funzionali

_Anche questi verificabili, non aggettivi. Se non riuscite a dire come lo
misurereste, non è ancora un requisito._

| #    | Requisito                                                                                                                                   |
| ---- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| RNF1 | Configurazioni di dispositivi, intervalli del simulatore e soglie sono esterne al codice e persistono dopo il riavvio.                      |
| RNF2 | L'indisponibilità di una sorgente non impedisce la consultazione delle misure già persistite e viene registrata separatamente.              |
| RNF3 | Acquisizione, elaborazione, persistenza e consultazione sono separate tramite interfacce definite.                                          |
| RNF4 | Credenziali e parametri di connessione non sono inclusi nel codice o nei log; le funzioni protette richiedono autenticazione.               |
| RNF5 | Le API validano gli input e applicano autorizzazione lato server; AI e presa visione non alterano le misure originali.                      |
| RNF6 | Installazione, configurazione, avvio e verifica dello stato sono descritti in istruzioni riproducibili con dati simulati adeguati ai trend. |
| RNF7 | Lo stato distingue problemi di singole sorgenti da problemi generali della piattaforma.                                                     |
| RNF8 | Prestazioni, disponibilità, volume massimo e retention sono da definire dopo aver chiarito frequenze e numerosità.                          |

## Vincoli dichiarati dal cliente

Non è richiesto il collegamento a sensori fisici; sono ammessi dati artificiali.
Il cliente non impone uno specifico database o framework. I sensori iniziali
sono simulati via software. I dati sono esposti alla dashboard tramite
interfacce definite; la soluzione è predisposta a future integrazioni reali.
I dati di prova devono dimostrare ricerche e trend temporali. L'AI è solo di
supporto e non modifica dati originali né sostituisce le soglie. Sono inoltre
richiesti simulatore multi-dispositivo, dashboard, gestione anomalie,
documentazione API/formato dati e istruzioni operative.

## Glossario

_Solo se nella richiesta cliente ci sono termini di dominio che userete spesso
e che non sono ovvi fuori da questo progetto._

| Termine       | Significato                                                                            |
| ------------- | -------------------------------------------------------------------------------------- |
| Soglia        | Valore configurato usato per determinare un superamento.                               |
| Anomalia      | Evento generato quando una misura soddisfa una regola di superamento di Soglia.        |
| Presa visione | Registrazione della consultazione di un'anomalia; non equivale a risoluzione.          |
| Telelettura   | Acquisizione remota da sistemi o dispositivi reali, prevista come integrazione futura. |
