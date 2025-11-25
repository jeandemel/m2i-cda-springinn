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


## Spring Boot

### Gestion globale des exceptions
Spring boot en Web va catch automatiquement les RuntimeException qui ne sont pas catchées mais produira des erreurs HTTP 500.
Comme notre Business throw des exceptions business lors de certains traitements, on va vouloir faire en sorte de catch ces exceptions dans la couche contrôleur afin de renvoyer les codes et messages d'erreurs pertinents.

Première solution simple, mais qui alourdira beaucoup les contrôleurs : faire des try catch dans chaque méthode et y gérer l'exception :

```java
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisplayRoomDTO add(@RequestBody @Valid CreateRoomDTO room) {
      try {
        
        return mapper.toDisplay(
            roomBusiness.createRoom(mapper.toEntity(room))
        );
      } catch (RoomNumberUnavaibleException e) {
        throw new ResponseStatusExcpetion(HttpStatus.BAD_REQUEST, e.getMessage());
      }
        
    }
```

Ça fonctionne, mais on va se retrouver à pas mal se répéter, notamment pour les NOT_FOUND.

Spring Boot permet donc de créer des Controller Advice, qui sont des classes décorées avec une annotation spéciale qui permettront de définir des exception handlers : un comportement généralisée à avoir face à une exception ou une autre.

Niveau organisation de ces handlers, on a le choix, on peut faire plusieurs classe ControllerAdvice selon les types d'exceptions ou d'entité par exemple, ou bien un ControllerAdvice pour tout le business comme ci dessous dans lequel on fait une méthode de handling par exception, il est également possible de regrouper plusieurs exceptions ensembles si elles ont un parent commun et résultat d'erreur similaire. (et enfin, on peut même techniquement faire un seul handler pour la classe parent qui ensuite fera des conditions pour définir l'erreur à renvoyer mais pour le coup, ça peut être un peu sale)

```java
@RestControllerAdvice
public class BusinessExceptionController {

    @ExceptionHandler(RoomNumberUnavaibleException.class)
    public ProblemDetail roomNumberUnavailable(RoomNumberUnavaibleException exception) {
        return ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail ressourcenotFound() {
        return ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, "Ressource could not be found");
    }
}
```



## Instructions

### Les DTO pour les routes Rooms existantes + mise en place des tests
1. Installer MapStruct dans Maven avec la dépendance + le plugin
2. Créer un ListRoomDTO avec id,number,price et capacity ainsi qu'un CreateRoomDTO avec number,price et capacity, les 3 obligatoires, le price et la capacity positifs. Faire le RoomMapper avec pour l'instant ses deux méthodes.
3. Côté RoomController, venir faire les conversions d'entité à DTO et inversement. Le business ne connait pas les DTO, juste les entités.
4. Créer une classe de test pour le RoomBusinessImpl en mode test unitaire, qui va donc créer un Mock pour le RoomRepository et une instance de RoomBusinessImpl auquel on donne le mock à manger (soit en faisant une instance à la mano, soit en utilisant un @InjectMocks sur la propriété)
5. Par principe, faire un test de getRoomPage qui va juste vérifier que le findAll est appelé une fois (surtout pour voir si les tests passent au final)
6. Faire 2 tests pour le createRoom un dans lequel on indique au mock du repository de renvoyer un optional empty lors de son find et dans lequel on vérifie que save a été appelé, et un dans lequel on dit au mock de renvoyer une valeur et alors il faudra tester qu'on a bien une exception du bon type levée


### Le reste du CRUD pour la Room
1. Mettre à jour l'implémentation du RoomBusiness pour y ajouter les trois méthodes : getOneRoom, deleteRoom, updateRoom
2. Les trois vont appeler le findOne() avec un orElseThrow() (qui est du coup catché et traité dans notre BusinessExceptionController)
3. Pour le getOneRoom, rien de particulier, juste on appel le find avec son orelse
4. Pour le delete, on fait la mếme, mais ensuite on le delete et pareil pour l'update qui va juste refaire la vérification du room number avant d'update
5. Côté contrôleur, on rajoute les 3 méthodes et un UpdateRoomDTO avec les 3 propriétés mais qui peuvent toutes être null ce coup ci
6. On modifie le mapper pour y ajouter le apply et on fait les mapping dans le contrôleur
7. Côté test, soit en unitaire, soit en fonctionnel, soit les deux, on vérifie que nos méthodes se comportent comme il faut en vérifiant si le delete ou le save sont appelés. Et dans la partie fonctionnelle, on peut vérifier les status http selon ce qu'on donne comme url

### Admin des room
#### Affichage des rooms
1. Créer une nouvelle page src/app/admin/room/page.tsx
2. Créer un component src/components/feature/admin/rooms-table.tsx dans lequel on va utiliser la Data Table de Antd pour afficher la liste des room.
3. On vient créer un lib/api/types.ts dans lequel on crée nos types correspondant aux DTO de notre back
4. Créer un lib/api/room-api.ts dans lequel on vient créer une fonction fetchRoomPage où on lance un axios.get vers notre /api/room et qui va donc récupérer un Page<DisplayRoom>
5. Dans le rooms-table.tsx on vient rajouter une Props de type Page<DisplayRoom>
6. Dans le room/page.tsx on fait notre requête fetchRoomPage et on donne le résultat à notre RoomsTable
7. dans le RoomsTable, on vient définir un tableau représentant la définition des columns à afficher : `[{title:'Number', dataIndex:'number', key:'number'},{title:'Capacity', dataIndex:'capacity', key:'capacity'},{title:'Price', dataIndex:'price', key:'price'}]`
8. On donne les columns et les data (la props) au composant Table

#### Ajout d'une room via une modal
1. Dans le room/page.tsx, rajouter le typage PageProps et récupérer les searchParams
2. Dans les searchParams on va récupérer spécifiquement une variable newRoom et dans le template, faire que si elle est truthy on affiche une Modal, sinon on l'affiche pas (la Modal a une propriété open={} qui permet de dire si elle est affichée ou non)
3. Ajouter un NextLink Add Room qui rajoutera newRoom=true dans l'url
4. Créer un nouveau components/features/admin/room-form.tsx qui sera un component client
5. Dans celui ci on fait un formulaire avec react-hook-form qui renverra un CreateRoom
6. On rajoute un postRoom(room:CreateRoom) qui va faire un axios.post<DisplayRoom> 
7. Dans room-form, on appel le postRoom au moment du submit

#### Validation de la disponibilité de la Room Number
1. Côté Spring, modifier la requête du RoomRepository findByNumber pour faire plutôt un un findByNumberOrId avec un @Query au dessus qui ira chercher par number ou id mais avec un seul argument de type String dans la méthode
2. Modifier dans le RoomBusinessImpl là où on avait appelé findByNumber pour le remplacer par la nouvelle méthode (mettre à jour les tests aussi)
3. Dans le 