FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

RUN ls -lh target

FROM eclipse-temurin:17
WORKDIR /app

COPY --from=build /build/target/aws-service*.jar aws-service.jar

EXPOSE 9010

CMD ["java", "-jar", "aws-service.jar"]
