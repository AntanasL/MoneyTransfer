# MoneyTransfer
Simplified Spring-less RESTful API for money transfers between accounts.

## Run application
```
    mvn clean install exec:java
```
The application runs on port 8888

## Run tests
```
    mvn clean verify
```
## Implementation details
* Spring-less
* In-memory datastore
* Executable as standalone (no server/container required)

## Data model
#### Account
```
    {
        "accountNumber": String,
        "amount": float
    }
```

#### Transfer
```
    {
        "id": String,
        "accountFrom": String
        "accountTo": String
        "amount": float
    }
```

## Functionality
#### POST /account 
#### create an account
Example:
```
    curl -v -X POST http://localhost:8888/account -d "{\"accountNumber\":\"1\", \"amount\" : \"100\"}"
```
#### GET /account/{accountNumber} 
#### read an account
Example:
```
    curl -v -X GET http://localhost:8888/account/1
```
#### POST /transfer 
#### transfer money from one account to another
Example:
```
    curl -v -X POST http://localhost:8888/transfer -d "{\"id\":\"1\", \"accountFrom\" : \"1\", \"accountTo\" : \"2\",  \"amount\" : \"10\"}"
```
Transfer will only be successful if
* source account exists
* target account exists
* there is a sufficient balance on the source account