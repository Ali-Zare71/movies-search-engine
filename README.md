# Movie Search Task

## Requirements

Create an MVP for a movie search to see how well a given set of technologies would work together.

There should be two Rest-API endpoints to:

* upload movie data to the service, that should be indexed to ElasticSearch. The upload from the JSON file should be
  triggered by calling the endpoint. The movies should first be pushed to RabbitMQ and from there to Elasticsearch.
* search for a movie and return all matches.

The API and the indexing service should communicate asynchronously using RabbitMQ. We should be able to launch the whole
application with docker-compose, which should include the necessary services e.g RabbitMQ and ElasticSearch. There is no
restriction on the programming language, feel free to use the one you are most comfortable with. Make use of solid
principles while designing the application.

## Data

Please use the attached json file (movies.json) for indexing. You can just focus on the fields `actors` and `title`.

[Sample Data](readme-data/movies.json)
## Architecture

This project was developed using a microservice architecture. there are three main services. I used **RabbitMQ** to
connect my services and **Elasticsearch** as the search engine. you can see the architecture in the below figure:

![Alt text](readme-data/architecture.jpg?raw=true "Figure 1: Architecture")

Services details are:

1. **ingestion-api-service:** This service receives movies data using a restfull api and put the data to **RabbitMQ**.

2. **index-service:** This service receives data from **RabbitMQ** and indexes them to **Elasticsearch**.

3. **search-service:** This service receives user's queries and provide related results (movies) using **Elasticsearch**
   .

**Development Stack:** Microservice Architecture, Java, Spring Boot, Elasticsearch, RabbitMQ, Swagger, Docker, Docker
Compose, Maven, Intellij IDE

## Build and Run

I used Maven to manage dependencies and build the project, and designed docker files and docker compose to build and run
the project easily. So you must have maven, docker and docker compose installed in your system and follow this steps to
run the project:

**1. Get the project from git:** You can get the project from GitHub using `clone` command.

```
git clone https://github.com/Ali-Zare71/movies-search-engine.git
```

**2. Build all services:** You can build all services by running `mvn package` command in the root folder of project.

```
cd movie-search-engine
mvn package
```

**3. Run the project:** After building the project, you can easily build docker images of services and run them and
their dependencies (like Elasticsearch and RabbitMQ) using **docker-compose**.

```
docker-compose up
```

Docker will download all of needed images (in the first run) and run the project so this phase may take a while, and you
have to wait until all the services are up.

## How to use?

To use the project you have to insert the data with the standard format to the system and try to retrieve related movies
regarding given queries. I have designed two restfull apis to achieve this. one endpoint for data entry and the other
one to fetch data:

**1. Data entry:**
You can import data to the project using the movies endpoint implemented in ingestion-api-service which is easily
accessible from swagger ui with this address:

http://localhost:8000/swagger-ui/

In this endpoint you can upload a file or directly send the json data by post requests.

upload a json file:
http://localhost:8000/api/movies/upload

upload json text:
http://localhost:8000/api/movies/

Here are the screenshots of how to use swagger:
![Alt text](readme-data/enrty-endpoint.jpg?raw=true "Figure 2: Entry endpoint")

Upload file:

![Alt text](readme-data/upload-file.jpg?raw=true "Figure 3: Upload file")

Upload json text:

![Alt text](readme-data/json-input.jpg?raw=true "Figure 4: Json input")

**2. Fetch data:**
You can fetch the data regarding a given query using the movies endpoint implemented in search-service which is easily
accessible from swagger ui with this address:

http://localhost:8001/swagger-ui/

Endpoint address:
http://localhost:8001/api/movies

Example:
http://localhost:8001/api/movies?query=Alec%20Baldwin&page=1&size=10&fuzzy-search=true&or-query=true

Here is the screenshot of fetch movies endpoint:

![Alt text](readme-data/fetch-endpint.jpg?raw=true "Figure 4: Json input")

this endpoint gets some parameters:

| parameter | description |
| :---:   | :---: |
| query  | user query to search   |
| page  | results page number (default: 1)   |
| size  | results number in each page (default: 10)   |
| fuzzy-search  | enables fuzzy search to retrieve more results (default: true)   |
| or-query  | enables or query search to retrieve more results (default: true)   |

## Todo

This items can be done in next versions:

* Add security to apis
* Use more fields and futures in ranking phase
* Test ranking results using a dataset and tune the parameters
