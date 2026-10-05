# Whitbread Basket &micro;Service

The existing limitations of the Opera system, plus the requirements for interoperability of the
Whitbread booking
platform advocate for the introduction of a basket/cart feature to the existing solution. The basket
would allow a user
to select multiple offerings, of different types (i.e. stay reservations, concert tickets. WI-FI
packages etc.) and pay
for all of them once. All purchased items would be identifiable by a single reference that is
managed and owned by the
Whitbread systems.

The basket feature will be modeled as a distinct micro-service that will interact mainly with the
Reservation Entity
micro-service to manage any interactions performed by the end-users against a stay definition. Based
on the user’s
selection, the Reservation entity will be responsible for creating the necessary number of cart
items in the basket (
i.e. one Opera reservation for each room). The same Reservation micro-service must provide all the
details that are
relevant for the items placed in the basket.

## Requirements

All services should:

* Include _**.gitignore**_
* Include the following section in _src/main/resources/application.yml_

```
info:
     build:
       groupId: @project.groupId@
       artifact: @project.artifactId@
       description: @project.description@
       version: @project.version@
       git:
         branch: @git.branch@
         commit:
           id: @git.commit.id@
           time: @git.commit.time@
           message: @git.commit.message.short@
```

* Have it's own package, (e.g. uk.co.whitbread.basket)

## How to run

### Run as a Spring Boot local application

`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local`
disables all
integration tools like Eureka or Configservice making sure the project runs in isolation/

### Local DynamoDB for development

You can start up the local Dynamo DB by running docker-compose up in the root directory. This will
start an instance on port 8000 which you can use locally.

Note: For podman users, make sure to allow local directories to be mounted on the VM if you want to
use the shared initial database by running:

`podman machine init --cpus=4 --disk-size=60 --memory=6096 -v $HOME:$HOME`

You can also run the DynamoDB JAR locally but make sure to configure the correct connection details
in application.yml

`java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb`

In order to create/update/etc. the database, one can use the AWS CLI.

Also, this is needed to set up the key and secret which will be used in the application properties
of the Spring Boot app. The key and secret MUST be set before you can access the db either via CLI
or programatically. This is done using `aws configure`.

To create the initial structure, use:

```
aws dynamodb create-table \
--table-name newBasket \
--attribute-definitions AttributeName=threeLetterHotelId,AttributeType=S AttributeName=sortKey,AttributeType=S AttributeName=reference,AttributeType=S \
--key-schema AttributeName=threeLetterHotelId,KeyType=HASH AttributeName=sortKey,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
--global-secondary-indexes \
        "[
            {
                \"IndexName\": \"referenceIndex\",
                \"KeySchema\": [{\"AttributeName\":\"reference\",\"KeyType\":\"HASH\"}],
                \"Projection\":{
                    \"ProjectionType\":\"KEYS_ONLY\"
                },
                \"ProvisionedThroughput\": {
                    \"ReadCapacityUnits\": 10,
                    \"WriteCapacityUnits\": 5
                }
            }
        ]" \
--table-class STANDARD \
--endpoint-url http://localhost:8000
```
```
aws dynamodb create-table \
--table-name PrepaidBookingCharges \
--attribute-definitions AttributeName=paymentNo,AttributeType=N AttributeName=reservationId,AttributeType=S \
--key-schema AttributeName=reservationId,KeyType=HASH AttributeName=paymentNo,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
--table-class STANDARD \
--endpoint-url http://localhost:8000
```

Note: Only key attributes have to be defined upon creation.

### Local Kafka for development

You can start up Kafka by running docker-compose up or podman-compose up

Useful commands:
```
podman exec -it kafka /bin/bash
cd /opt/kafka/bin
./kafka-topics.sh --bootstrap-server localhost:9092 --list
./kafka-console-consumer.sh --topic my-topic --from-beginning --bootstrap-server localhost:9092
```

### Local Unleash for development

You can start up Unleash by running docker-compose up or podman-compose up

## Hexagonal Architecture

`domain` - Models the domain of the API

`ports` - Defines any primary ports which act as input to the API and any secondary ports which are
external
dependencies/resources

`adapters` - Implementations of ports

`config` - Package that contains any Spring Bean configuration

## Project Folder Structure

The arrangement below provides the structure within which the development effort will be
accomplished.

    .
    ├── docs                        # Documentation files
    ├── infrastructure              # Infrastucture related configuration
    │   └── docker-local            # Defining and running the systems on which the application depends
    ├── src                         # Application sources
    │   ├── main                    # Source files
    │   └── test                    # Test files
    ├── .gitignore                  # Files/folders to be ignored by Git
    ├── google-checkstyle.xml       # Checkstyle configuration
    ├── pom.xml                     # Configuration details used by Maven to build the project
    └── README.md                   # Brief description of underlying GitHub project

## Technologies

- [Spring cloud microservices parent v2.0.0](https://github.com/whitbread-eos/spring-cloud-microservice-parent/releases/tag/v2.0.0)
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates
