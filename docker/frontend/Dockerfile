FROM node:22-alpine AS build
WORKDIR /app

# 1. Copia dipendenze ed esegui install
COPY frontend/package*.json ./
RUN npm install

# 2. Copia i sorgenti del frontend
COPY frontend/ .

# 3. Copia la configurazione
COPY config/config.json /config.json
COPY config/ /app/config/

# 4. Esegui la build in modalità development (senza restrizioni di budget)
RUN npm run build -- --output-path=dist --configuration development

# Stage 2: Serve con Nginx
FROM nginx:alpine
COPY --from=build /app/dist/*/browser /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]