## Synopsis

Java program to connect to specified GitHub organization and check for members that are missing the "name" value in their profile. The user will be email to update their profile. A file listing this members will be created and uploaded to a Amazon S3 bucket.

## Prerequisites

- Java 8 Runtime
- Maven 3
- Administrative account for a Github organization
- Account created in Amazon IAM with AmazonS3FullAccess and AWSConnected policies
- Amazon credentials stored in recommended location 
    Linux/Unix - ~/.aws/credentials
    Windows - C:\Users\USERNAME\.aws\credentials

## Program Properties
This requires a properties files as a program argument. A templateThe properties file "app.properties" can be found at the root of project directory. All values are required.

## Build with Maven
mvn clean verify package

You can specify a profile to select logging to either Console or Rolling Files (default is console)
Log files directory will be created in the location of the jar file.

mvn -Pconsole clean verify package
mvn -Prolling clean verify package

## Execution
Executing this program requires two program arguments:
1. Absolute path to property file containing required values for the program
2. Name of the Github organization to check for members with missing names

java -jar github-name.jar "property file" "github org"