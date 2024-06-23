# autoacervus

### Deploy

1. Assine um serviço de hospedagem (ou tenha uma máquina Linux ociosa)
2. Tenha um domínio configurado para apontar para o IP da máquina que hospedará o autoacervus
3. Instale Docker e Docker Compose
4. Clone o repositório
5. Altere `.env`
6. Copie `src/main/resources/application.properties.example` para `src/main/resources/application.properties` e edite de acordo. Também altere o item `command` do serviço `certbot` em `docker-compose.yml`
7. Edite `nginx/autoacervus.conf` e `nginx/autoacervus.after.conf` de acordo
8. Execute `docker compose up -d --build mysql java nginx`
9. Execute `docker compose up -d certbot`
10. Execute `crontab -e` e adicione uma nova entrada: `0 0 1 */2 * docker start autoacervus-certbot; docker restart autoacervus-nginx`
11. Execute `mv nginx/autoacervus.after.conf nginx/autoacervus.conf`
12. Reinicie o contêiner do nginx com `docker compose restart nginx`

### Self host sem deploy

1. Repita os passos 3, 4, 5 e 6 das instruções de Deploy
2. Execute `docker compose up -d --build mysql java`
