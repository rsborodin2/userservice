# USERSERVICE #

github: https://github.com/screen190795/userservice
<br>

dockerHub: https://hub.docker.com/u/rborodin

## Описание ##

Данный сервис входит в проект SkillGram и реализует функционал управления пользователями и подписками социальной сети  SkillGram.
Благодаря сервису мы можем выполнять CRUD-операции над сущностями подписок и пользователей.

## Стек технологий ##

Java 11, Gradle, Spring Boot, Postgres database, Spring data JPA

## Выполнено ##

* Добавлен initial sql и .sh скрипт для развертывания БД;
* Добавлены основные Endpoint-ы для REST API для управления пользователями и подписками; 
* Добавлен Dockerfile для упаковывания сервиса в контейнер.
## Сборка и запуск ##
Для сборки используется используется команда gradle clean bootjar, файл кладется в projectDir\build\libs, для запуска используем java -jar {jarname}.jar

## Docker ##
Данный сервис запускается контейнером в Doker наряду с другим контейнером rborodin/userservice-db