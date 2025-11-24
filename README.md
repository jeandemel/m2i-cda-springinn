# SpringInn

Projet de gestion d'hôtel avec des chambres et des réservations.

## Concepts abordés
* Docker / Conteneurisation
* Couche Business

## Conteneur de déploiement
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

### Conteneur de développement
Dans ce projet, on utilise l'extension et la spécification devcontainer de Microsoft (également disponible sur IntelliJ). Cet outil permet de créer un conteneur de développement dans lequel sera ouver l'IDE et qui permettra de développer directement à l'intérieur du conteneur, éliminant ainsi le besoin d'avoir l'environnement d'exécution et de dév installé sur sa machine, juste Docker est suffisant.

Pour le mettre en place, on crée un dossier [.devcontainer](.devcontainer) avec un fichier [devcontainer.json](.devcontainer/devcontainer.json) et un fichier [docker-compose-dev.yml](.devcontainer/docker-compose-dev.yml)

Techniquement on pourrait n'avoir que le fichier devcontainer.json et venir y décrire le conteneur à créer pour le développement, on peut y spécifier les images à utiliser, les ports à exposer etc.

Dans notre cas, on est parti pour réutiliser une partie de notre environnement décrit dans le docker-compose.yml original dont on redéfini certaines partie avec le docker-compose-dev.yml

Dans notre devcontainer.json, on vient donc indiquer qu'on charge les deux fichiers yml (attention à l'ordre, on charge le dev après l'original) et qu'on vient lancer VsCode dans le conteneur/service "app"

`devcontainer.json`
```json

{
  //Le nom du conteneur à créer
	"name": "SpringInn DevContainer",
  //Les fichier docker compose qui serviront pour la création du conteneur
	"dockerComposeFile": [
		"../docker-compose.yml",
		"docker-compose-dev.yml"
	],

  //Le service/conteneur définit dans les docker-compose auquel on veut connecter vscode
	"service": "app",
  //le dossier du conteneur dans lequel on viendra positionner vscode, ici le nom du dossier qu'on expose au conteneur dans le docker-compose
	"workspaceFolder": "/app"

}

```


`docker-compose-dev.yml`
```yml
services:
 
  app:
    # on redéfinit le build pour ne pas utiliser notre DockerFile
    build: !reset null
    # à la place on part sur une image java
    image: eclipse-temurin:25-jdk
    #on expose notre code source dans le conteneur sous le dossier /app
    volumes:
      - .:/app
    #on execute une commande pour que le conteneur ne se ferme pas automatiquement
    command: sleep infinity
```

On peut également personnaliser le conteneur pour y installer certaines extensions vscode ou features : 


```json

{
  //...reste de la config

	"features": {
		"ghcr.io/davzucky/devcontainers-features-wolfi/bash:1": {}, //installe /bin/bash dans le conteneur
		"ghcr.io/devcontainers/features/git:1": { //install git dans le conteneur
			"ppa": true,
			"version": "system"
		}
		
	},

	
	"customizations": {
		"vscode": {
      //Installe les extensions java, spring et un client mysql
			"extensions": [
				"vscjava.vscode-java-pack",
				"cweijan.vscode-mysql-client2",
				"vmware.vscode-boot-dev-pack"
			]
		}
	}
}
```
## Entités

```plantuml

class Room {
    id:string
    number:string
    capacity:int
    price:double
}

class Booking {
    id:string
    startDate:LocalDate
    duration:int
    total:double
    guestCount:int
}

class User {
    id:string
    email:string
    password:string
    role:string
}
class Customer extends User {
    address:string
    name:string
    firstname:string
    phoneNumber:string
}


Room "*" -- "*" Booking
Booking "*" -- "1" Customer


```

Nous avons donc un User qui servira pour les admins et les clients. Les clients sont représentés par la classe Customer qui hérite de User qui pourra avoir plusieurs réservations, ces réservations peuvent contenir une ou plusieurs chambres. On répète le total du prix dans la réservation pour prendre en compte les changements de prix futur des chambres. On utilisera le startDate et la duration des réservations pour déterminer quelles chambres sont libres ou non.

## Architecture
![Diagramme d'architecture](diagrammes/architecture-ntier.png)

On part ici sur une architecture n-tiers dans laquelle on fait en sorte de bien séparer les différentes responsabilités: 
* Les Entités et les Repositories ne s'occupent que de la persistence des données
* Le Business utilise les repositories, certains services et les entités et contiendra le gros de l'algorithmie pour les différentes fonctionnalités de l'application, c'est dans cette couche que seront les règles métiers notamment
* Les Services contiennent de l'algorithmie propre à l'infrastructure comme l'envoie de mail, la génération et validation de Tokens si on fait des JWT, etc.
* Les Contrôleurs définissent les routes et les DTO d'entrée et de Sortie, se chargent du mapping et ne font appel qu'au business ou à certains services. Ils ne contiennent quasiment aucune algorithmie
* On fait en sorte de définir des interfaces pour les Business et Service afin d'avoir les différentes couches qui dépendront d'abstractions plutôt que d'implémentation.




## Instructions

### Les DTO pour les routes Rooms existantes + mise en place des tests
1. Installer MapStruct dans Maven avec la dépendance + le plugin
2. Créer un ListRoomDTO avec id,number,price et capacity ainsi qu'un CreateRoomDTO avec number,price et capacity, les 3 obligatoires, le price et la capacity positifs
3. Côté RoomController, venir faire les conversion d'entité à DTO et inversement. Le business ne connait pas les DTO, juste les entités.
4. Créer une classe de test pour le RoomBusinessImpl en mode test unitaire, qui va donc créer un Mock pour le RoomRepository
5. Par principe, faire un test de getRoomPage qui va juste vérifier que le findAll est appelé une fois (surtout pour voir si les tests passent au final)
6. Faire 2 tests pour le createRoom un dans lequel on indique au mock du repository de renvoyé un optional empty lors de son find et dans lequel on vérifie que save a été appelé, et un dans lequel on dit au mock de renvoyer une valeur et alors il faudra tester qu'on a bien une exception du bon type levée
