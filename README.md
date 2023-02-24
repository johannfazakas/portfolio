# Portfolio
Service to check the GitHub repositories by user

## Prerequisites

### Java 17
Running `java --version` on the root directory should indicate JDK 17. 

## Run

### Local
This will start the service on port 8532 on your local.  
`./gradlew clean bootRunLocal`

## Test

### Automatic testing

Run both unit & integration tests:  
`./gradlew clean check`

Run unit tests:  
`./gradlew clean test`

Run integration tests:  
`./gradlew clean integrationTest`

### Manual testing

First, [start the service on your local](#local).  
If you are using IntelliJ, check the [porfolio.http](/portfolio.http) file and select the "local" environment to use.  
You should be able to easily run the prepared requests by hitting the "Play" buttons on the left.  
If you want to change any environment parameters, check the [http-client.env.json](/http-client.env.json) file.  

## To do

### Add authentication for a higher rate limit
The last prepared request in [porfolio.http](/portfolio.http) currently fails.  
This happens as the user has many repositories to get the branches for and doing this exceeds GitHub Api rate limit.  
The first fix to be tried is adding authentication, authenticated requests have a higher rate limit in GitHub.  
So an application should be registered in GitHub, and based on a client id & secret, the service should be able to obtain access tokens to be used on requests.  
(This would open the path for other topics: access tokens should be cached between requests, also refreshed when needed.)  
Will touch this part soon...
