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

## Authentication token & testing databases

The JSON token definition needs to be set in a file named `chpl/chpl-api-for-dummies/src/main/resources/environment.auth.properties`. There is a file named `chpl/chpl-auth/src/main/resources/environment.auth.properties.template` that has the format. Copy that file and change the `keyLocation` key to something local.

Two files are used for testing purposes. There are template files for each of them that will need to be copied and renamed, with valid local data inserted:

```
chpl/chpl-auth/src/test/resources/environment.auth.test.properties
chpl/chpl-api-for-dummies/src/test/resources/environment.test.properties
```

# Calling the REST API
Let's walk through the different parts of a sample HTTP GET REST call, which can be done programmatically through Java code or an application such as Postman.
For example: https://chpl.healthit.gov/rest/search?searchTerm=
In order to successfully make the above REST call, the following must be defined:

HTTP method = GET

Headers:
API-key: 12909a978483dfb8ecd0596c98ae9094
Content-Type: application/json

When calling the API programmatically, it is best practice to define sensitive information, including the API-key, username and password, via a local environment.properties file. Within the SampleApp GitHub, there is an environment.properties.template example file. Rename this file to environment.properties, and update the parameter values as appropriate.