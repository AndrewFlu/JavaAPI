# JavaAPI
Learn API automation in Java

## Запуск тестов в Docker-контейнере
1. Скачать образ, который будет взят за основу образа для запуска тестов:  
`docker pull maven:3.6.3-openjdk-14`
2. Создать `Dockerfile` и заполнить его инстркуциями:  
   `FROM maven:3.6.3-openjdk-14`   
   `WORKDIR /tests`   
   `COPY . .`  
   `CMD mvn clean test`  
3. Создать образ(image), выполнив команду  
`docker build -t java-api-tests .`  
, где "." — означает, что `Dockerfile` нужно искать в текущей директории  
"-t" — сокращение от _tag_
4. Запустить контейнер, выполнив команду  
`docker run --rm --mount type=bind,src=$(pwd),target=/tests/ java-api-tests`  
