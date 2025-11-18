# SpringInn

Projet de gestion d'hôtel avec des chambres et des réservations.

## Concepts abordés
* Docker / Conteneurisation
* Couche Business

## Conteneur d'exécution
* [Dockerfile](Dockerfile) qui définit un stage de build qui permet de générer le jar du projet ainsi qu'un stage d'exécution qui utilise ce jar pour lancer l'application (utiliser des stages permet de réduire la taille finale de l'image docker)

* [docker-compose.yml](docker-compose.yml) qui charge le Dockerfile, expose les ports et va également définir un autre service/conteneur pour la base de données (ici mariadb)

```yml
services:
  # définition du service java/spring
  app:
    # qui build en utilisant le fichier Dockerfile situé à la racine
    build: .
    # expose sur le port 8080 de notre machine le port 8080 du conteneur
    ports:
      - 8080:8080
    # On inidique que ce conteneur dépend du service/conteneur database et que celui ci soit considéré comme "healthy"
    depends_on:
      database:  
        condition: service_healthy
  # définition du service mariadb
  database:
    image: mariadb:lts
    environment:
      - MARIADB_DATABASE=springinn #le nom de la base de donnée créée dans le conteneur
      - MARIADB_USER=dev #le nom du user mariadb qui se connectera à la bdd
      - MARIADB_PASSWORD=1234 #son mot de passe
      - MARIADB_RANDOM_ROOT_PASSWORD=true #on génère un mot de passe random pour le root (on pourrait aussi le laisser vide)

    #on définit un healthcheck qui permettra à l'autre conteneur de savoir quand celui ci est prêt
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"] #le healthcheck inclut dans l'image mariadb
      start_period: 10s #au bout de combien de temps on commence à vérifier s'il tourne
      interval: 10s #le temps entre chaque check
      timeout: 5s #au bout de combien de temps on considère que le test n'est pas passé
      retries: 3 #le nombre d'essais pour voir si le service marche
```


Dans le [application.properties](src/main/resources/application.properties) on vient définir notre lien de connexion à la base de données en prenant en compte que ce sera exécuté dans le conteneur, on vient donc indiqué le host du conteneur mariadb comme cible de connexion et les informations qu'on a indiqué dans les variables d'environnement

```
spring.datasource.url=jdbc:mariadb://database:3306/springinn?user=dev&password=1234
```

Pour lancer l'application, on utilise `docker compose up`