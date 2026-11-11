# Kubernetes Setup per EnergyMonitor

## Cosa è stato fatto
- Rimossi i manifest di deployment e service per PostgreSQL da k8s: il database ora gira solo in un container Docker, come da richiesta.
- Verificati e mantenuti i manifest per backend (`deployment-backend.yaml`, `service-backend.yaml`, `configmap.yaml`, `ingress.yaml`).
- Creati i manifest mancanti per il frontend: `deployment-frontend.yaml` e `service-frontend.yaml`.

## Perché
- Il database PostgreSQL non deve essere gestito da Kubernetes, ma solo tramite Docker Compose, per semplicità e coerenza con l'ambiente di sviluppo.
- Il backend e il frontend sono deployati su Kubernetes per sfruttare orchestrazione, scalabilità e gestione centralizzata.

## Come provare k8s
1. **Avvia PostgreSQL**:
   - Da root repo: `docker compose up -d postgres`
2. **Costruisci le immagini** (se non già presenti):
   - Backend: `docker build -t energymonitor-backend:local ./backend`
   - Frontend: `docker build -t energymonitor-frontend:local ./frontend`
3. **Importa le immagini in MicroK8s** (se usi MicroK8s):
   - `microk8s ctr images import energymonitor-backend.tar`
   - `microk8s ctr images import energymonitor-frontend.tar`
4. **Applica i manifest**:
   - `kubectl apply -f k8s/`
5. **Accedi all'applicazione**:
   - Assicurati che l'host `energymonitor.local` punti all'IP del cluster (modifica `/etc/hosts` se necessario).
   - Apri il browser su `http://energymonitor.local/`.

## Note
- Verifica che le variabili d'ambiente e i secret siano corretti.
- Se usi Docker Desktop, assicurati che i pod possano raggiungere il container PostgreSQL (potrebbe essere necessario configurare la rete).


- Ho creato il dockerfile per il fontend