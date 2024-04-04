FROM openjdk:21
WORKDIR /app
COPY . /app
CMD ["java", "-jar", "target/scrapper.jar"]
