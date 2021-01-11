### Statement

See the [exercise statement](Statement.pdf)

### Stack
mvn, spring boot, rest, with swagger and H2 as in memory-database

### Build / Run

1. From terminal windows or git bash, go to the project root folder `campsite`

2. To build (compile and run unit/integration tests)

        mvn clean install

3. To run

- Either launch the class **CampsiteApplication** from your favorite IDE (IntelliJ or Eclipse)
- run below cmd

        java -jar target/scallable-campsite-1.0.0-SNAPSHOT.jar & 
 - then to check:
    - restfull API, go to http://localhost:8081/api.campsite/swagger-ui.html
    - Database content (user/pwd = upgrade/upgrade), go http://localhost:8081/api.campsite/h2-console
 
