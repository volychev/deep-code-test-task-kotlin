FROM eclipse-temurin:21-jdk-alpine
WORKDIR /usr/src/subscription

COPY . .

RUN ./gradlew build -x test -Pdebug=false
