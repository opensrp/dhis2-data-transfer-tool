# OpenSRP DHIS2 Data Transfer
## Overview
This tool transfers data from DHIS2 to OpenSRP. Currently, it supports:
1. Importing organization units and levels as OpenSRP locations and location tags respectively.
2. Exporting DHIS2 indicators to csv file to be used on OpenSRP apps.

## Build from source
```shell
./gradlew jar
```

## Run/Execute

- Create indicators `.csv` file to use for his2 integration on an OpenSRP app.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -export i -ds <DataSetName>
```
- Import organization units levels as location tags to OpenSRP server.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -import t
```
- Import organization units as locations to OpenSRP server.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -import l
```
- Import organization units and organization unit levels to OpenSRP server.
```shell
java -jar opensrp-dhis2-data-transfer-0.0.1-SNAPSHOT.jar -import lt
```

## Configurations
Configurations are stored in `src/main/resources/application.properties`, if `application.properties` file is not found on .jar or source files working directory the latter has more precedence. Update accordingly.

| Configuration                  | Description                                              | Type    | Default               |
|--------------------------------|----------------------------------------------------------|---------|-----------------------|
| opensrp.url                    | OpenSRP server url                                       | String  | http://localhost:8080 |
| opensrp.username               | OpenSRP user username                                    | String  | demo                  |
| opensrp.password               | OpenSRP user password                                    | String  | password              |
| mopensrp.keycloak.url          | keycloak server url                                      | String  | https://keycloak-url  |
| opensrp.keycloak.realm         | keycloak realm                                           | String  | realm                 |
| opensrp.keycloak.client.id     | keycloak client id                                       | String  | client-id             |
| opensrp.keycloak.client.secret | keycloak client id                                       | String  | client-secret         |
| dhis2.url                      | dhis2 server url                                         | String  | https://dhis2-url     |
| dhis2.username                 | dhis2 user name                                          | String  | user                  |
| dhis2.password                 | dhis2 user password                                      | String  | district              |
| indicator.csv.name             | indicator export csv name                                | String  | indicators.csv        |
| location.tag.suffix            | adds suffix to location tag name                         | String  |                       |
| location.tag.getIdByName       | updates location tag by name, if found in OpenSRP server | Boolean | true                  |

Imports are idempotent!!
