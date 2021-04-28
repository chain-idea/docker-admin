FROM node:12.20.1-alpine3.10
WORKDIR /tmp/build

RUN npm config set registry http://registry.npm.taobao.org/

ADD pages/package.json .
RUN npm install
ADD pages/ .
RUN npm run build

FROM maven:3.6.3

WORKDIR /tmp/build

ADD pom.xml .
ADD src/main/java/com/gzqylc/BootApplication.java src/main/java/com/gzqylc/BootApplication.java
RUN mvn -q -DskipTests=true  package


ADD src ./src
COPY --from=0 /tmp/build/dist/ src/main/resources/static/
RUN mvn -q -DskipTests=true package \
        && mv target/*.jar /app.jar \
        && cd / && rm -rf /tmp/build

FROM openjdk:8-alpine
COPY --from=1 /app.jar /app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/app.jar"]