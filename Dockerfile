# Etapa 1: Construcción (Build)
FROM maven:3.8.5-openjdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM tomcat:10.1-jdk17-openjdk-slim
# Eliminar las apps por defecto de Tomcat para que no interfieran
RUN rm -rf /usr/local/tomcat/webapps/*
# Copiar el archivo WAR generado a la carpeta webapps de Tomcat como ROOT.war
COPY --from=build /app/target/librorank.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
