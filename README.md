# chpl-api-for-dummies
Provides an example of an application that calls the CHPL REST Service

# Installation instructions

## Install required software

Java 1.8.0
Eclipse (latest)

## Clone the repository
Using Git Bash, run the following command:

```sh
$ git clone https://github.com/chpladmin/chpl-api.git
```

## Add external JARs to project

fluent-hc-4.5.2.jar
java-json.jar
gson-2.8.0.jar
log4j-api-2.6.2.jar
httpclient-4.5.2.jar
httpcore-4.4.4.jar
commons-logging-1.2.jar
commons-csv-1.2.jar
commons-codec-1.9.jar
commons-lang3-3.1.jar
commons-io-2.5.jar
httpmime-4.5.2.jar
httpclient-win-4.5.2.jar

# Calling the REST API
Let's walk through the different parts of a sample HTTP GET REST call, which can be done programmatically through Java code or an application such as Postman.
For example: https://chpl.healthit.gov/rest/search?searchTerm=
In order to successfully make the above REST call, the following must be defined:

HTTP method = GET

Headers:
API-key: 12909a978483dfb8ecd0596c98ae9094
Content-Type: application/json

When calling the API programmatically, it is best practice to define sensitive information, including the API-key, username and password, via a local environment.properties file. Within the SampleApp GitHub, there is an environment.properties.template file. Rename this file to environment.properties, and update the parameter values as appropriate.