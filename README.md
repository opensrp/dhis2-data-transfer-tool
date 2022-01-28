# Opensrp DHIS2 Data Transfer

## Build from source
```shell
./gradlew jar
```

## Run/Execute

- Create indicators .csv file to use for his2 integration on an opensrp app.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -export i -ds <DataSetName>
```
- Import organization units levels as location tags to opensrp server.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -export t
```
- Import organization units as locations to opensrp server.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -export l
```

## Configurations
Configurations are store in `src/main/resources/application.properties`, update to your match your environment.

| Configuration                  | Description                                              | Type    | Default               |
|--------------------------------|----------------------------------------------------------|---------|-----------------------|
| opensrp.url                    | opensrp server url                                       | String  | http://localhost:8080 |
| opensrp.username               | opensrp user username                                    | String  | demo                  |
| opensrp.password               | opensrp user password                                    | String  | password              |
| mopensrp.keycloak.url          | keycloak server url                                      | String  | https://keycloak-url  |
| opensrp.keycloak.realm         | keycloak realm                                           | String  | realm                 |
| opensrp.keycloak.client.id     | keycloak client id                                       | String  | client-id             |
| opensrp.keycloak.client.secret | keycloak client id                                       | String  | client-secret         |
| dhis2.url                      | dhis2 server url                                         | String  | https://dhis2-url     |
| dhis2.username                 | dhis2 user name                                          | String  | user                  |
| dhis2.password                 | dhis2 user password                                      | String  | district              |
| indicator.csv.name             | indicator export csv name                                | String  | indicators.csv        |
| location.tag.suffix            | adds suffix to location tag name                         | String  |                       |
| location.tag.getIdByName       | updates location tag by name, if found in opensrp server | Boolean | true                  |

Imports are idempotent!!
