# Étape 1 : utiliser une image Java officielle
FROM eclipse-temurin:21-jdk-alpine

# Étape 2 : définir le répertoire de travail dans le conteneur
WORKDIR /app

## Étape 3 : copier le fichier jar dans le conteneur
#COPY build/libs/ogp-0.0.1.jar app.jar

# Copier tous les JAR générés dans build/libs
COPY build/libs/*.jar app.jar

# Étape 4 : exposer le port sur lequel l'application va tourner
EXPOSE 8080

# Étape 5 : définir la commande pour démarrer l'application
ENTRYPOINT ["java","-jar","app.jar"]

# Étape 1 : Build Gradle (image full Debian)
# Étape 1 : Build Gradle avec Java 17
#FROM eclipse-temurin:17-jdk AS build
#
#WORKDIR /app
#
#COPY . .
#
#RUN chmod +x gradlew && ./gradlew clean build -x test
#
## Étape 2 : Runtime (Alpine léger avec Java 21)
#FROM eclipse-temurin:21-jdk-alpine
#
#WORKDIR /app
#
#COPY --from=build /app/build/libs/*.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java","-jar","app.jar"]