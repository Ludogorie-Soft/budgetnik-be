# budgetnik-be

## How to run the app local:

 - create .env file in the root folder and put the necessary environment variables
 - put the necessary environment variables in the run configuration


 - run ```maven clean install``` in the terminal
 - run ```docker-compose up -d --build``` in the terminal


 - in application.properties file change in the first line -> database to localhost
 - in application.properties file set server.port=8082


 - in application.properties file change ddl-auto to create:
    ```spring.jpa.hibernate.ddl-auto=create```
 - run the app


 - in application.properties file change ddl-auto to update:
   ```spring.jpa.hibernate.ddl-auto=update```
 - run the app again


## Before running the tests

 - start the mail test server:
```
docker run -d -p 3025:8025 -p 1025:1025 mailhog/mailhog
```