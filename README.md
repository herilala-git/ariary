Ariary est un petit back-office de trésorerie.
- saisir des opérations (dépôt, change à terme, obligation)
- faire avancer (statut)
- importer le marché du jour
- calculer ce qu'elles valent.

Application Spring Boot, avec une base Oracle et un faux fournisseur de marché (WireMock), tous dans Docker.

## Ce qu'il faut

JDK 25, Maven, Docker Desktop, Eclipse et Git Bash.
Toutes les commandes se tapent dans Git Bash, à la racine du projet.

## Démarrer

La base Oracle, la première fois :

    docker run -d --name ariary-oracle -p 1521:1521 -e ORACLE_PASSWORD=oracle -e APP_USER=ariary -e APP_USER_PASSWORD=ariary --health-cmd healthcheck.sh --health-interval 10s gvenzl/oracle-free:23.26.3-slim-faststart

Elle est prête quand `docker ps` affiche "healthy".

Le fournisseur de marché, la première fois :

    MSYS_NO_PATHCONV=1 docker run -d --name ariary-wiremock -p 8081:8080 -v "$(pwd -W)/wiremock:/home/wiremock:ro" wiremock/wiremock:3.10.0

Les fois suivantes, après un redémarrage du PC : `docker start ariary-oracle ariary-wiremock`. Si `docker run`
répond que le nom est déjà utilisé, c'est que le conteneur existe déjà : il suffit de faire `docker start`.

L'application : dans Eclipse, clic droit sur AriaryApplication, puis Run As > Spring Boot App. Elle écoute sur le port 8080. Au premier démarrage, Flyway crée les tables et les données de départ.

Pour vérifier que tout répond :

    curl localhost:8080/api/v1/currencies

## Ce qu'il y a au départ

- La date du jour : le 03/09/2027, avec le calendrier de Toronto.
- Trois portefeuilles : PF-CASH pour les dépôts, PF-FX pour le change, PF-BONDS pour les obligations.
- Trois contreparties : CP-BOC, CP-BANKA et CP-BANKB.

Il n'y a encore ni opération ni marché.

## Un premier tour

Créer un dépôt. Il reçoit le numéro 1000, puis les suivants auront 1001, 1002...

    curl -X POST localhost:8080/api/v1/operations/deposits -H "Content-Type: application/json" -d '{"portfolio":"PF-CASH","counterparty":"CP-BOC","currency":"CAD","notional":1000000,"rate":0.045,"dayCount":"ACT_360","startDate":"2027-09-03","maturityDate":"2028-09-15"}'

Importer le marché du jour, puis demander la valeur du dépôt :

    curl -X POST "localhost:8080/api/v1/market/import?date=2027-09-03"
    curl localhost:8080/api/v1/operations/1000/value

Le faire avancer d'un statut :

    curl -X POST localhost:8080/api/v1/operations/1000/validate

Voir toutes les opérations :

    curl localhost:8080/api/v1/operations/blotter

## Le cycle de vie

Une opération passe par NEW, puis VALIDATED, puis CONFIRMED, puis SETTLED. On peut l'annuler (cancel) tant qu'elle n'est pas SETTLED. Son notionnel ne se change (PATCH) qu'en NEW.
