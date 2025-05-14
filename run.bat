@echo off
REM Define MongoDB connection string
set URI_DB=mongodb://localhost:27017/ferramentas

REM Run the Spring Boot application
mvnw.cmd spring-boot:run
