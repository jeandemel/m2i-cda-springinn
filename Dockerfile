FROM eclipse-temurin:25-jdk-alpine AS builder
COPY . /app
WORKDIR /app
RUN ./mvnw -Dmaven.test.skip=true package

FROM eclipse-temurin:25-jdk-alpine
COPY --from=builder /app/target/springinn-0.0.1-SNAPSHOT.jar springinn.jar
ENTRYPOINT [ "java", "-jar", "/springinn.jar" ]


# build l'image avec
# docker build --tag=springinn:latest .

# lancer le conteneur en mode détaché avec
# docker run -d -p 8080:8080 --name springinn springinn:latest

# si le conteneur springinn existe déjà, exécuter ça avant de relancer le run
# docker rm springinn

# aller sur http://localhost:8080 et bien kiffer le json