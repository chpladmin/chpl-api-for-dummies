# CHPL Sample Application
Provides an example of an application that calls the CHPL REST Service

# Installation instructions

## Install required software

Java 1.8.0
mvn 3.3.3

## Clone the repository
Using Git Bash, run the following command:

```sh
$ git clone https://github.com/chpladmin/sample-application.git
```

# How to Call the REST API

First, please obtain a valid API-key by registering on the CHPL Resources page at https://chpl.healthit.gov/#/resources 

The CHPL REST API documentation may also be reached at https://chpl.healthit.gov/#/resources which provides information on possible API calls, HTTP Method, parameters, etc. 

Let's walk through the different parts of a sample HTTP GET REST call, which can be performed programmatically through Java code or an application such as Postman.
For example: https://chpl.healthit.gov/rest/search?searchTerm=
In order to successfully make the above REST call, the following must be defined:

HTTP method = GET

Headers:
API-key: 12345a987654abc1abc1234a12ab1234
Content-Type: application/json

When calling the API programmatically, it is best practice to define sensitive information, including the API-key, username and password, via a local environment.properties file. Within the SampleApp GitHub, there is an environment.properties.template file. Rename this file to environment.properties, and update the parameter values as appropriate.

In the SampleApp, although example #4 uses authentication with the token obtained in example #1, please note that authentication with a token is not a requirement as most API calls do not require authentication. 

Also note that in the SampleApp, the API-key is truncated to 8 characters for display purposes.

# How to Run SampleApp using Maven
Once the environment.properties has been configured, open Git Bash (or application of your choice), navigate to the SampleApp directory where the pom.xml is located and execute the following command:
mvn package
This will create two jars in the /target directory: 
1. sampleApp-1.0-SNAPSHOT.jar
2. sampleApp-1.0-SNAPSHOT-jar-with-dependencies.jar
Navigate to the /target directory and execute the following command to run the application:
java -jar sampleApp-1.0-SNAPSHOT-jar-with-dependencies.jar