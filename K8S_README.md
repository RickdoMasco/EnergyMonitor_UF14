# Kubernetes — EnergyMonitor

Questa guida descrive l'avvio locale su Windows con Kubernetes di Docker
Desktop. Kubernetes esegue backend e frontend; PostgreSQL resta in un
container Docker Compose sull'host. I manifest del repository non installano
né il cluster Kubernetes né l'Ingress Controller.

> Per la procedura di rilascio, la checklist e il rollback vedi
> [docs/deployment.md](docs/deployment.md). Per le variabili d'ambiente vedi
> [docs/configuration.md](docs/configuration.md).

## Architettura

- Backend Spring Boot: Deployment e Service sulla porta `8080`.
- Frontend Angular servito da Nginx: Deployment e Service sulla porta `80`.
- PostgreSQL: Docker Compose, porta `5433` sull'host e `5432` nel container.
- Ingress NGINX: inoltra `/api` al backend e `/` al frontend.
- Il ConfigMap usa `host.docker.internal:5433` per raggiungere PostgreSQL da
  Kubernetes di Docker Desktop.

## Prerequisiti

- Docker Desktop installato, con Kubernetes abilitato e avviato.
- `docker` e `kubectl` disponibili nel terminale.
- Ingress Controller NGINX installato nel cluster nel namespace
  `ingress-nginx`. Il file `k8s/ingress.yaml` configura solo la risorsa
  applicativa Ingress, non installa il controller.
- File `config/dev.env` configurato. Se non esiste, dalla root del repository
  copiare `.env.example` in `config/dev.env` e impostare i valori PostgreSQL
  come descritto in [docs/configuration.md](docs/configuration.md).

Verifica che il contesto Kubernetes selezionato sia quello di Docker Desktop
e che il service dell'Ingress Controller esista:

```powershell
kubectl config current-context
kubectl get service ingress-nginx-controller -n ingress-nginx
```

## Avvio

Esegui i comandi seguenti dalla root del repository.

### 1. Avvia PostgreSQL

```powershell
docker compose --env-file .\config\dev.env up -d postgres
docker compose --env-file .\config\dev.env ps
```

Attendi che il servizio PostgreSQL risulti `healthy`. La porta host
`POSTGRES_PORT` in `config/dev.env` deve essere `5433`, in accordo con
`k8s/configmap.yaml`.

### 2. Crea e applica il Secret locale

Il file `k8s/secret-example.yaml` è un template: non applicarlo direttamente,
perché la password è un segnaposto. Creane una copia locale:

```powershell
Copy-Item k8s/secret-example.yaml k8s/energymonitor-postgres-secret.local.yaml
notepad k8s/energymonitor-postgres-secret.local.yaml
```

Imposta nel file locale `POSTGRES_DB`, `POSTGRES_USER` e
`POSTGRES_PASSWORD` agli stessi valori di `config/dev.env`. Mantieni il nome
del Secret `energymonitor-postgres-secret`, poi applica soltanto la copia
locale:

```powershell
kubectl apply -f k8s/energymonitor-postgres-secret.local.yaml
```

Il file `k8s/energymonitor-postgres-secret.local.yaml` è escluso da Git.
Non committarlo e non inserire credenziali reali nei manifest versionati.

### 3. Costruisci le immagini

Genera il JAR del backend e costruisci la relativa immagine:

```powershell
.\backend\gradlew.bat -p backend bootJar
docker build -f docker/backend/Dockerfile -t energymonitor-backend:local ./backend
```

Il Dockerfile frontend legge `config/config.json` per generare la build
Angular. Crea la configurazione dal template, sostituisci il valore
segnaposto con la licenza PrimeNG valida e costruisci l'immagine dalla root:

```powershell
Copy-Item config.json.example config/config.json
notepad config/config.json
docker build -f docker/frontend/Dockerfile -t energymonitor-frontend:local ./
```

`config/config.json` è escluso da Git. In Docker Desktop le immagini locali
sono usate dai Deployment con `imagePullPolicy: IfNotPresent`. Per cluster
diversi da quello di Docker Desktop, rendi le immagini accessibili al runtime
del cluster prima del deployment.

### 4. Applica i manifest applicativi

Applica i manifest uno per volta. Non usare `kubectl apply -f k8s/`: quel
comando includerebbe anche `secret-example.yaml`, che potrebbe sovrascrivere
il Secret locale con la password segnaposto.

```powershell
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment-backend.yaml
kubectl apply -f k8s/deployment-frontend.yaml
kubectl apply -f k8s/service-backend.yaml
kubectl apply -f k8s/service-frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

Attendi che entrambi i Deployment siano pronti e controlla le risorse:

```powershell
kubectl rollout status deployment/energymonitor-backend --timeout=180s
kubectl rollout status deployment/energymonitor-frontend --timeout=180s
kubectl get pods
kubectl get services
kubectl get ingress
```

I pod dovrebbero risultare `1/1 Running`. La readiness probe del backend
richiede una risposta positiva da `GET /api/simulators`, quindi anche
PostgreSQL deve essere raggiungibile.

### 5. Accedi all'applicazione

In un terminale dedicato, inoltra la porta del controller Ingress:

```powershell
kubectl -n ingress-nginx port-forward service/ingress-nginx-controller 8080:80
```

Lascia il port-forward in esecuzione e apri `http://localhost:8080/`.
L'Ingress instrada:

- `/` al frontend Angular;
- `/api/...` al backend Spring Boot.

Puoi verificare le risposte da un altro terminale:

```powershell
curl.exe -I http://localhost:8080/
curl.exe http://localhost:8080/api/simulators
```

## Configurazione PostgreSQL

`k8s/configmap.yaml` fornisce `SPRING_PROFILES_ACTIVE`, host, porta e nome del
database. Le credenziali sono fornite dal Secret
`energymonitor-postgres-secret`, che deve corrispondere a `config/dev.env`.
La porta `5433` è quella pubblicata sull'host da Docker Compose; PostgreSQL
continua ad ascoltare sulla porta `5432` all'interno del container.

## Diagnostica

### Stato e log

```powershell
kubectl get pods
kubectl describe pod -l app=energymonitor-backend
kubectl logs deployment/energymonitor-backend --tail=100
kubectl logs deployment/energymonitor-frontend --tail=100
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller --tail=100
```

Se il backend è in `CrashLoopBackOff`, controlla anche i log del container
precedente:

```powershell
kubectl logs deployment/energymonitor-backend --previous --tail=100
```

Cause frequenti:

- PostgreSQL non è avviato o non è ancora `healthy`;
- host, porta o credenziali del Secret non corrispondono alla configurazione
  Docker Compose;
- `host.docker.internal` non è raggiungibile dal cluster;
- il Secret applicato ha il nome errato o contiene ancora il valore
  segnaposto;
- il JAR non è stato rigenerato prima di costruire l'immagine backend;
- il cluster non vede l'immagine locale usata dal Deployment.

### Porta locale `8080` occupata

Controlla quale processo ascolta sulla porta:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
  Select-Object OwningProcess,LocalAddress,LocalPort
```

Se esiste già un port-forward, usa quello o interrompilo con `Ctrl+C` nel
terminale che lo esegue. In alternativa inoltra una porta locale diversa:

```powershell
kubectl -n ingress-nginx port-forward service/ingress-nginx-controller 8081:80
```

Poi apri `http://localhost:8081/`.

### Pagina predefinita di Nginx

Se appare `Welcome to nginx!`, il pod potrebbe usare una vecchia immagine
frontend. Ricostruiscila e riavvia il Deployment:

```powershell
docker build -f docker/frontend/Dockerfile -t energymonitor-frontend:local ./
kubectl rollout restart deployment/energymonitor-frontend
kubectl rollout status deployment/energymonitor-frontend --timeout=180s
```

Il Dockerfile corrente installa la configurazione Nginx per la SPA e copia
`/app/dist/browser` in `/usr/share/nginx/html/`.
