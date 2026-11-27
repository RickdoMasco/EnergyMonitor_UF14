# Node 24 LTS include npm 11, la stessa versione usata per generare package-lock.json
FROM node:24-alpine AS build
WORKDIR /app

# 1. Copia dipendenze ed esegui install
COPY frontend/package*.json ./
RUN npm ci

# 2. Copia i sorgenti del frontend
COPY frontend/ .

# 3. Copia solo la configurazione necessaria al frontend (niente file .env con credenziali)
COPY config/config.json /config.json

# 4. Esegui la build di produzione (ottimizzata, con controllo dei budget)
RUN npm run build -- --output-path=dist

# Stage 2: Serve con Nginx
FROM nginx:alpine
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/browser /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]