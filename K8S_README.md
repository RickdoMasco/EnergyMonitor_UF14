# Kubernetes Setup per EnergyMonitor

Questa configurazione usa Kubernetes per backend e frontend. PostgreSQL resta gestito da Docker Compose all'esterno del cluster.

## Architettura

- Backend Spring Boot: pod Kubernetes, porta `8080`
- Frontend Angular: pod Kubernetes con Nginx, porta `80`
- PostgreSQL: container Docker Compose, porta pubblica `5433`
- Ingress NGINX: instrada `/` al frontend e `/api` al backend
- `host.docker.internal`: consente ai pod Docker Desktop di raggiungere il database dell'host

## Avvio completo da Windows

### 1. Avvia PostgreSQL

Dalla root del repository:

```powershell
docker compose --env-file .\config\dev.env up -d postgres
```

Verifica che il database sia sano:

```powershell
docker compose --env-file .\config\dev.env ps
```

Il servizio deve risultare `Up` e `healthy`, con la porta `5433` pubblicata.

### 2. Compila il backend

```powershell
.\backend\gradlew.bat -p backend bootJar
```

Costruisci l'immagine locale:

```powershell
docker build -f docker/backend/Dockerfile -t energymonitor-backend:local ./backend
```

### 3. Costruisci il frontend

La build viene eseguita dal Dockerfile, che copia i file Angular nella directory servita da Nginx:

```powershell
docker build -f docker/frontend/Dockerfile -t energymonitor-frontend:local ./
```

### 4. Applica i manifest Kubernetes

```powershell
kubectl apply -f k8s/
```

Ricontrolla lo stato:

```powershell
kubectl get pods
kubectl get services
kubectl get ingress
```

Entrambi i pod devono risultare `1/1 Running`:

```text
energymonitor-backend   1/1   Running
energymonitor-frontend  1/1   Running
```

### 5. Avvia l'Ingress NGINX

Apri un terminale dedicato e avvia il port-forward:

```powershell
kubectl -n ingress-nginx port-forward svc/ingress-nginx-controller 8080:80
```

Lascia il processo attivo e usa nel browser:

```text
http://localhost:8080/
```

## Routing

- `http://localhost:8080/` → frontend Angular
- `http://localhost:8080/api/...` → backend Spring Boot

Ad esempio:

```powershell
curl.exe -I http://localhost:8080/
curl.exe http://localhost:8080/api/simulators
```

Entrambe le richieste devono restituire una risposta HTTP valida.

## Configurazione del database

Il ConfigMap usa:

```text
POSTGRES_HOST=host.docker.internal
POSTGRES_PORT=5433
POSTGRES_DB=energymonitor
```

Le credenziali sono memorizzate nel Secret Kubernetes `energymonitor-postgres-secret`.

Il repository contiene soltanto `k8s/secret-example.yaml`, con valori fittizi. Non committare mai un file reale contenente credenziali.

### Creare il Secret locale

Da PowerShell, dalla root del repository:

```powershell
Copy-Item k8s/secret-example.yaml k8s/energymonitor-postgres-secret.local.yaml
notepad k8s/energymonitor-postgres-secret.local.yaml
```

Nel file locale modifica i valori senza modificare il nome `energymonitor-postgres-secret`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: energymonitor-postgres-secret
  namespace: default
type: Opaque
stringData:
  POSTGRES_DB: "energymonitor"
  POSTGRES_USER: "energymonitor"
  POSTGRES_PASSWORD: "INSERISCI_LA_PASSWORD_LOCALE"
```

I valori devono coincidere con il database inizializzato da Docker Compose in `config/dev.env`. Applica quindi soltanto il file locale:

```powershell
kubectl apply -f k8s/energymonitor-postgres-secret.local.yaml
kubectl rollout restart deployment/energymonitor-backend
kubectl rollout status deployment/energymonitor-backend --timeout=180s
```

Il file `k8s/energymonitor-postgres-secret.local.yaml` è ignorato da Git tramite `.gitignore` e non deve essere aggiunto ai commit.


## Verifica dei log

Backend:

```powershell
kubectl logs deployment/energymonitor-backend --tail=100
```

Frontend:

```powershell
kubectl logs deployment/energymonitor-frontend --tail=100
```

Ingress:

```powershell
kubectl logs -n ingress-nginx deployment/ingress-nginx-controller --tail=100
```

## Porta locale già in uso

Se il port-forward segnala che la porta `8080` è occupata, verifica il processo:

```powershell
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
  Select-Object OwningProcess,LocalAddress,LocalPort
```

Se il processo è `kubectl`, potrebbe esistere già un port-forward precedente. Apri direttamente `http://localhost:8080/` oppure interrompi il processo con `Ctrl+C` nel terminale che lo mantiene attivo.

In alternativa usa una porta libera:

```powershell
kubectl -n ingress-nginx port-forward svc/ingress-nginx-controller 8081:80
```

Apri quindi `http://localhost:8081/`.

## Pagina predefinita di Nginx

Se il browser mostra:

```text
Welcome to nginx!
```

ricostruisci il frontend e riavvia il pod:

```powershell
docker build -f docker/frontend/Dockerfile -t energymonitor-frontend:local ./
kubectl rollout restart deployment/energymonitor-frontend
kubectl rollout status deployment/energymonitor-frontend --timeout=180s
```

Questa pagina indica normalmente che nell'immagine o nel pod è presente il file predefinito di Nginx, non i file Angular. Il Dockerfile corretto copia `/app/dist/browser` in `/usr/share/nginx/html/`.

## Backend in CrashLoopBackOff

Controlla lo stato:

```powershell
kubectl get pods
kubectl describe pod -l app=energymonitor-backend
kubectl logs deployment/energymonitor-backend --previous --tail=100
```

Cause comuni:

- PostgreSQL non è stato avviato;
- il Secret contiene credenziali diverse da Docker Compose;
- il ConfigMap usa la porta sbagliata;
- `host.docker.internal` non è raggiungibile dal cluster;
- l'immagine backend è stata ricostruita senza rigenerare il JAR.

La procedura minima corretta è:

```powershell
docker compose --env-file .\config\dev.env up -d postgres
.\backend\gradlew.bat -p backend bootJar
docker build -f docker/backend/Dockerfile -t energymonitor-backend:local ./backend
kubectl apply -f k8s/
kubectl rollout restart deployment/energymonitor-backend
kubectl rollout status deployment/energymonitor-backend --timeout=180s
```
