# Star War API

> May the force be with you!

## Description

Star Wars API is a Rest API to store and retrieve fantastic data of Star Wars franchise.

## Features

- **Register Planets** with an enrichment feature (currently how many movies the Planet was appeared);
- **List all** registered Planets in a nice paginated way;
- Get a Planet **by ID**;
- Get a Planet **by Name**;
- **Delete** a Planet by ID;

# Getting Started

## Environment

You'll need to setup your environment with:

- **Docker** (developed for: >= 20.10.3);
- **Docker Compose** (developed for: >= 1.25.5);
- **Java** (developed for: 11)
- **Maven** (developed for: >= 3.6.3)

## Run

Next, you just need to run two commands:

```shell
./mvnw clean package -DskipTests
```

Then, run the application and its dependencies

```shell
docker-compose -f docker-compose.yml -f docker-compose.api.yml up
```

**Tip:** put `-d` at the end to run in background.

### Running only the dependencies

If you want to run the API from your IDE or directly from terminal, you can run just the dependencies:

```shell
docker-compose -f docker-compose.yml up
```

## Swagger

With the application running, you can access the Swagger UI to interact with the API:

- http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## Test

Run:

```shell
docker-compose -f docker-compose.yml up -d
```

Then:

```shell
./mvnw clean test
```
