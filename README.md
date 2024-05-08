#  Spring Exercise

This project is a sample of Java application, intended to provide a solution to the exercise described at [REDACTED]

It uses several key technologies
- Spring Boot 
- Spring HTTP interface; new with Spring Framework 6 / Spring Boot 3. Declarative HTTP services, used here to access resources at [REDACTED]. I am well familiar with using the Spring Reactive WebClient and older (deprecated RestTemplate) for accessing REST services but decided to use an alternative on this occasion.  
- Spring Data JPA; for persisting data 
- Lombok; for annotated processing to generate various types of code (e.g. getters / setters, constructors, loggers, etc)
- Wiremock; for stubbing and simulation of server responses

### Prerequisites

In order to run and test the application, you will need the following

- JDK 21
- Maven

Optional
- Your favourite IDE, e.g., IntelliJ IDEA.

### Installation / Running the project

To run the Spring Boot app, this can be done in the normal way with the maven spring boot plugin. The value for the API is set from an environment variable. It can be set in as shown.

The server starts on port 8080 so this must be available, although it can be overridden. This also starts up the H2 instance.

`mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DSECRET_API_KEY=[insert the API KEY]"`

## Usage

The REST endpoint can be tested with the following curl commands.

`curl -i -X POST --location "http://localhost:8080/companyofficers?activeOnly=false" \
-H "Content-Type: application/json" \
-d '{ "companyNumber": "10241297" }'`

`curl -i -X POST --location "http://localhost:8080/companyofficers?activeOnly=true" \
-H "Content-Type: application/json" \
-d '{ "companyName": "BBC" }'`


Note that the project also includes a file named "endpoints-test.http" in the "intellij-http" folder. When opened in IntelliJ, it contains sample HTTP requests which can be used to do ad-hoc testing on the service.

# Some implementation notes

## Comments
There are probably more comments in this code than I would normally write. 

## Integration Tests
These are named with an "IT" extension and are executed in the maven "verify" rather than the test phase. This is fairly standard practice. In the context of this project, anything which starts the Spring Boot application context is deemed an integration test. The main areas where this happens is 
- Calling REST endpoint, where MockMvc is used to prove endpoint and some failure handling on a running server
- Calling the Spring HTTP interface to hit the TruProxy API, uses WireMock to stub the responses
- Database repository, in tandem with an in-memory H2 database. 

## Asynchronous Work
There were two main areas where I felt work could be passed into other threads 
- Collection of officer details for each company. If we have many companies from a name search, officers could be retrieved in parallel. 
- Saving company and officer details to the database. This is done with a CompletableFuture running asynchronously

## Database / Data Model
Not knowing the real identity for each successive OFFICER coming from TruProxy, I adopted a crude approach of using the Name / Appointed date to uniquely identify the officer. Without this, each time you hit the remote service and Hibernate saves, it doesn't know if the officer is new or the same as one saved so new officers would be saved each time.


## Running the tests

`mvn clean test` runs all unit tests
`mvn clean verify` is a later phase and also runs ITs

## Database

For simplicity, a file-based H2 instance was chosen. I considered running postgres from docker but for the purposes of the exercise, this would impose an extra dependency. 
When the application is running, the instance can be connected to via the h2-console at http://localhost:8080/h2-console/ 
In any real-world scenario, the password would be made available from a secret. It is set to a default given in the application.yml but could be overridden from the environment.


# H2
If you want to check the database operations when running the service, the connection parameters are as follows 
- Driver Class : org.h2.Driver
- JDBC URL : jdbc:h2:file:./db/demo
- User : sa
- Password : password

## Troubleshooting

- "403 FORBIDDEN from GET" reported in the console. If a request is issued and the console reports this error then it is probably that the API key (`SECRET_API_KEY`) has not been set properly in the environment running the service.

## Contact

chris.faulkner@gmail.com


