# Procedura di rilascio e rollback

|                        |                                            |
| ---------------------- | ------------------------------------------ |
| **Progetto**           | Energy Monitor — AlpEnergia Servizi S.p.A. |
| **Team**               | TEnergy                                    |
| **Data**               | 25/11/2026                                 |
| **Versione documento** | 0.1                                        |

## 1. Ambiente di pubblicazione

L'applicazione gira su **Kubernetes di Docker Desktop**, namespace `default`.
PostgreSQL resta fuori dal cluster, in un container Docker Compose sull'host.

| Componente               | Dove                                | Porta                      |
| ------------------------ | ----------------------------------- | -------------------------- |
| Backend Spring Boot      | Deployment `energymonitor-backend`  | 8080                       |
| Frontend Angular (Nginx) | Deployment `energymonitor-frontend` | 80                         |
| PostgreSQL               | Docker Compose, fuori dal cluster   | 5433 host / 5432 container |
| Ingress NGINX            | namespace `ingress-nginx`           | 80                         |

**Hostname:** non assegnato. L'accesso avviene via `port-forward` su
`http://localhost:8080/`. Vedi §2.

## 2. Come si arriva dall'esterno

Ingress NGINX di Kubernetes. Routing per **path** sullo stesso host:

- `/` → Service `energymonitor-frontend` (porta 80)
- `/api` → Service `energymonitor-backend` (porta 8080)

L'Ingress **non ha regola `host:`** e **non termina TLS**: il traffico è HTTP
in chiaro. L'accesso dall'esterno avviene con:

```powershell
kubectl -n ingress-nginx port-forward service/ingress-nginx-controller 8080:80
```

Poi `http://localhost:8080/`. Dettagli in [K8S_README.md](../K8S_README.md).

## 3. Procedura di rilascio

Comandi dalla root del repository. Sostituire `<versione>` con un tag
semantico (es. `1.0.0`).

1. Verificare che i test passino:

    ```powershell
    .\backend\gradlew.bat -p backend test
    ```

2. Costruire il JAR del backend:

    ```powershell
    .\backend\gradlew.bat -p backend bootJar
    ```

3. Costruire le immagini con tag di versione:

    ```powershell
    docker build -f docker/backend/Dockerfile -t energymonitor-backend:<versione> ./backend
    docker build -f docker/frontend/Dockerfile -t energymonitor-frontend:<versione> ./
    ```

4. Aggiornare i Deployment:

    ```powershell
    kubectl set image deployment/energymonitor-backend energymonitor-backend=energymonitor-backend:<versione>
    kubectl set image deployment/energymonitor-frontend energymonitor-frontend=energymonitor-frontend:<versione>
    ```

5. Osservare il rollout:

    ```powershell
    kubectl rollout status deployment/energymonitor-backend --timeout=180s
    kubectl rollout status deployment/energymonitor-frontend --timeout=180s
    ```

6. Verificare (vedi §6):

    ```powershell
    curl.exe -I http://localhost:8080/
    curl.exe http://localhost:8080/api/simulators
    kubectl logs deployment/energymonitor-backend --tail=50
    ```

## 4. Procedura di rollback

**Criterio:** si esegue il rollback se il passo 6 non passa — l'app non
risponde, il routing è sbagliato, o i log mostrano errori nuovi riconducibili
al rilascio.

```powershell
kubectl rollout undo deployment/energymonitor-backend
kubectl rollout undo deployment/energymonitor-frontend
kubectl rollout history deployment/energymonitor-backend
```

Dopo il rollback, ripetere la verifica del passo 6 sulla versione precedente.

## 5. Rollback eseguito in laboratorio

**Non ancora eseguito.** Da compilare dopo aver provato un rilascio e un
rollback reali nel cluster.

## 6. Checklist di rilascio

### Prima

- [ ] Test backend superati (`.\backend\gradlew.bat -p backend test`)
- [ ] Immagini taggate con versione, non `:local`
- [ ] `config/dev.env` e `config/config.json` presenti e valorizzati
- [ ] Secret `energymonitor-postgres-secret` applicato e allineato a `config/dev.env`

### Durante

- [ ] Applicare un componente alla volta
- [ ] Osservare `kubectl rollout status` di ogni Deployment

### Dopo

- [ ] L'app risponde su `http://localhost:8080/`
- [ ] Routing corretto: `/` al frontend, `/api` al backend
- [ ] Nessun errore nuovo nei log

### Se qualcosa non va

- [ ] `kubectl rollout undo deployment/<nome>`
- [ ] Ripetere la verifica sulla versione precedente

## 7. Registro dei rilasci

| Data | Versione | Esito | Rollback eseguito? |
| ---- | -------- | ----- | ------------------ |
|      |          |       |                    |
