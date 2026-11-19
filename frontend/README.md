# Frontend

Applicazione Angular 22 standalone con routing, PrimeNG e Vitest.

## Avvio in sviluppo

Dalla root del repository, crea `config.json` dal template e imposta la chiave
PrimeNG nel campo `license`:

```powershell
Copy-Item config.json.example config.json
```

Poi, dalla cartella `frontend`, installa le dipendenze e avvia l'applicazione:

```powershell
npm ci
npm start
```

`npm start` genera `src/environments/environment.ts` a partire da
`config.json` e avvia Angular su `http://localhost:4200/`. Il proxy inoltra le
chiamate `/api` al backend locale su `http://localhost:8080`, che deve essere
già avviato. `config.json` e il file generato sono esclusi da Git.

## Build e test

Dalla cartella `frontend`:

```powershell
npm run build
npm test
```

I test usano Vitest tramite Angular CLI. Per i comandi completi di avvio e per
il contratto delle API implementate, consulta il [README principale](../README.md)
e [docs/API.md](../docs/API.md).

La dashboard e le relative route frontend sono presenti, ma i relativi
endpoint backend non fanno parte dell'API v0.1; il servizio API restituisce
quindi un errore esplicito finché tali endpoint non saranno implementati.
