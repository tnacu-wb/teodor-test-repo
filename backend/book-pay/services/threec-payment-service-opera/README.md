# threec-payment-service

The 3C Payment service is a Spring Webflux based microservice which provides a RESTful api
that allows the processing of Payment journeys through the 3C Web2Pay Payment Gateway.

![3C](https://www.ariane.com/images/content-2019/integrations/payment-system/3cpayment3B.webp)

| Type | Link  | Notes |
| --- | --- | --- |
| Github | https://github.com/whitbread-eos/threec-payment-service |  |
| Documentation | https://whitbreadis.atlassian.net/wiki/spaces/BE/pages/2660892794/3C-Payment-Service |  |
| Swagger | https://app.swaggerhub.com/apis/whitbread/3CPayment-API |  |

## Technologies

* Java 17
* Spring Webflux
* Dynamo DB

## Running Locally

![Spring Boot](https://i2.wp.com/www.thecuriousdev.org/wp-content/uploads/2017/12/spring-boot-logo.png?fit=600%2C315&ssl=1)

You should ensure the application has been built correctly and the required dependencies have been resolved.

`mvn clean install`

This service has a dependency on a local Dynamo DB and therefore you should follow the below "## Running Dynamo DB
Locally" instructions.

Once Dynamo is running you can run the `threec-payment-service` with the following local Spring profile

`-Dspring.profiles.active=local`

## Running Dynamo DB Locally

![DynamoDB](https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/DynamoDB.png/220px-DynamoDB.png)

A Docker compose file is located in the root directory. You should run this as follows

`docker-compose up -d`

Once you can see the container has started on port `8042` you can use the AWS Cli to run the following command.
Before that please configure AWS CLI (`aws configure`) with dummy credentials.

```
aws dynamodb --endpoint-url http://localhost:8042 create-table \
    --table-name threec-payment-dev \
    --attribute-definitions AttributeName=payment-id,AttributeType=S \
                            AttributeName=request-id,AttributeType=S \
    --key-schema AttributeName=payment-id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"request-id\",
                \"ProvisionedThroughput\":{
                    \"ReadCapacityUnits\":5,
                    \"WriteCapacityUnits\":5
                },           
                \"KeySchema\": [{\"AttributeName\":\"request-id\",\"KeyType\":\"HASH\"}],
                \"Projection\":{
                    \"ProjectionType\":\"ALL\"
                }
            }
        ]"
```

Once the table is created, you can enable ttl by running below command in terminal.

```
aws dynamodb --endpoint-url http://localhost:8042 update-time-to-live \
    --table-name threec-payment-dev --time-to-live-specification "Enabled=true, AttributeName=expire"
```

```
aws dynamodb --endpoint-url http://localhost:8042 delete-table \
    --table-name threec-payment-dev
```

This creates a Dynamo DB table called `threec-payment-dev` with a primary key called `paymentId`

You could use a tool such as https://aws.amazon.com/dynamodb/nosql-workbench/ as a client to interact with the DynamoDB.

****
If you have problems with DynamoDB local allowing multiple items with the same primary key, especially during updates
you will need
to delete the file under `docker/dynamodb` and restart your docker container to fix this.
****

CE terraform for DynamoDB in environment
-> https://github.com/whitbread-eos/wbd-cloud-engineering/tree/master/terraform/whitbread-microservices-k8s-eks/eks/databases/threec-dynamodb

## CCUI

## Allowing internet traffic to reach your localhost

The webhook endpoint requires opening up ports and making them accessible on your public IP address.
This isn't always possible, depending on your ISP and the access levels to your router.
However, there is a workaround in https://ngrok.com/
It creates a tunnel and exposes a port. All you need to do is point it to the correct port you want to expose, and it
will create a publicly accessible URL.
Update your local config in bootstrap.yml with this new URL.

## Swagger

Running locally on: http://localhost:9001/swagger-ui.html

Remote SwaggerHub: https://app.swaggerhub.com/apis/whitbread/3CPayment-API/1.0

## Diagrams

*To regenerate plantuml diagrams please use the command `mvn plantuml:generate`*

![Payments_ECOMM](docs/diagrams/images/payments_ecomm.png?raw=true "Ecommerce payments")

![Payments_MOTO](docs/diagrams/images/payments_moto.png?raw=true "GDS/Contact Centre payments")

![Payments Eckoh](docs/diagrams/images/payments_eckoh.png?raw=true "Eckoh payments")

## 3c-accounts

Managed in `providers.yml` and `DefaultProviderAccountFactory`.

```

provider:
accounts:

-

# Pay now GBP ecommerce transactions via iPage

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["PI", "BB", "APPS_IOS", "APPS_ANDROID"]
paymentSubTypes: ["ECOMM"]
configuration:
serviceAction: "3dspay"
newCardTemplate: "wb_newcard_pn_v4.xml"
savedCardTemplate: "wb_savedcard_pn_v4.xml"
newCardTrxOption: "G"
savedCardTrxOption: "P"
fraudScreened: true
fraudProfile: Web profile

-

# Pay now EUR ecommerce transactions via iPage

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["PI", "BB", "APPS_IOS", "APPS_ANDROID"]
paymentSubTypes: ["ECOMM"]
configuration:
serviceAction: "3dspay"
newCardTemplate: "wb_newcard_pn_v4.xml"
savedCardTemplate: "wb_savedcard_pn_v4.xml"
newCardTrxOption: "G"
savedCardTrxOption: "P"
fraudScreened: true
fraudProfile: Web profile

-

# Pay on arrival GBP ecommerce transactions via iPage

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["PI", "BB", "APPS_IOS", "APPS_ANDROID"]
paymentSubTypes: ["ECOMM"]
configuration:
serviceAction: "3dsauthorise"
newCardTemplate: "wb_newcard_poa_v4.xml"
savedCardTemplate: "wb_savedcard_poa_v4.xml"
newCardTrxOption: "G"
savedCardTrxOption: "P"
fraudScreened: true
fraudProfile: Web profile

-

# Pay on arrival EUR ecommerce transactions via iPage

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["PI", "BB", "APPS_IOS", "APPS_ANDROID"]
paymentSubTypes: ["ECOMM"]
configuration:
serviceAction: "3dsauthorise"
newCardTemplate: "wb_newcard_poa_v4.xml"
savedCardTemplate: "wb_savedcard_poa_v4.xml"
newCardTrxOption: "G"
savedCardTrxOption: "P"
fraudScreened: true
fraudProfile: Web profile

-

# Pay on arrival GBP PIBA ecommerce transactions via iPage

bookingType: "PAY_ON_ARRIVAL"
paymentType: "PIBA"
currency: "GBP"
channelTypes: ["PI", "BB", "APPS_IOS", "APPS_ANDROID"]
paymentSubTypes: ["ECOMM"]
configuration:
serviceAction: "authorise"
newCardTemplate: "wb_newcard_poa_piba_v4.xml"
savedCardTemplate: "wb_savedcard_poa_piba_v4.xml"
newCardTrxOption: "G"
savedCardTrxOption: "P"
fraudScreened: false

-

# Pay now GBP MOTO CCC transactions

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["CCC"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "payrequestnocardread"
fraudScreened: true
fraudProfile: Call Centre profile

-

# Pay now GBP MOTO GDS, FRONT_DESK transactions

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["GDS","FRONT_DESK"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "payrequestnocardread"
fraudScreened: false

-

# Pay now EUR MOTO CCC transactions

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["CCC"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "payrequestnocardread"
fraudScreened: true
fraudProfile: Call Centre profile

-

# Pay now EUR MOTO GDS,FRONT_DESK transactions

bookingType: "PAY_NOW"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["GDS", "FRONT_DESK"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "payrequestnocardread"
fraudScreened: false

-

# Pay on arrival GBP MOTO CCC transactions

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["CCC"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "EftAuthorization"
fraudScreened: true
fraudProfile: Call Centre profile

-

# Pay on arrival GBP MOTO GDS and front desk transactions

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "GBP"
channelTypes: ["GDS","FRONT_DESK"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "EftAuthorization"
fraudScreened: false

-

# Pay on arrival EUR MOTO CCC transactions

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["CCC"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "EftAuthorization"
fraudScreened: true
fraudProfile: Call Centre profile

-

# Pay on arrival EUR MOTO GDS and front desk transactions

bookingType: "PAY_ON_ARRIVAL"
paymentType: "CARD"
currency: "EUR"
channelTypes: ["GDS","FRONT_DESK"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "EftAuthorization"
fraudScreened: false

-

# Pay on arrival PIBA MOTO transactions - GBP

bookingType: "PAY_ON_ARRIVAL"
paymentType: "PIBA"
currency: "GBP"
channelTypes: ["CCC", "GDS", "FRONT_DESK"]
paymentSubTypes: ["MOTO","ECOMM"]
configuration:
serviceAction: "EftAuthorization"
fraudScreened: false

-

# Pay now PIBA front desk transactions - GBP

bookingType: "PAY_NOW"
paymentType: "PIBA"
currency: "GBP"
channelTypes: ["FRONT_DESK"]
paymentSubTypes: ["MOTO"]
configuration:
serviceAction: "payrequestnocardread"
fraudScreened: false

- # ECKOH

paymentSubTypes: [ "ECKOH" ]
paymentType: "CARD"
currency: "GBP"
configuration:
newCardTemplate: "N/A"

```

## Global Error Handling

All errors will be handled globally `GlobalErrorWebExceptionHandler` to ensure that
API responses are consistent in the below example format.

If a `PaymentServiceException` is handled the API Http Response code will be picked
from the one provided when creating this exception. If one is not found it will default to
a `500 Internal Server Error`. All error responses will contain an `errorCode` as standard from the
`PaymentServiceException`, otherwise a default will always be returned.

```

{
"timestamp": "2021-04-07T11:02:11.652+00:00",
"path": "/payments",
"message": "Transaction unsuccessful with provider reason [E777 Invalid token or token not found].",
"requestId": "e4b2ba97-1",
"errorCode": 6
}

```
