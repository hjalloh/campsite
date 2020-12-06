### Stack
mvn, spring boot, rest, with swagger and H2 as in memory-database

### Documentation

1. How to build

        mvn clean install

2. How to run

 then to check:
 -  restfull API, go to http://localhost:8081/api.campsite/swagger-ui.html
 - Database content (user/pwd = upgrade/upgrade), go http://localhost:8081/api.campsite/h2-console
 
 
 #### TODO
 - Test concurrency bookings
 - Test concurrency booking locking timeout 