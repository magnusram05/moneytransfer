# Money Transfer APIs

## Balance Inquiry 
### API 
http://localhost:8080/fintech/account/v1/balance

### HTTP Method
```POST```

### Payload
```json
{
	"userID" : 1,
	"accountID": 1
}
```

### Response
```json
{
    "errorMessages": null,
    "successMsgs": [
        "Account Balance: 1000"
    ],
    "account": {
        "accountID": 1,
        "userID": 1,
        "currentBalance": 1000
    },
    "errorCode": "SUCCESS"
}
```

## Money Transfer
### API
http://localhost:8080/fintech/account/v1/moneytransfer

### HTTP Method
```POST```

### Payload
```json
{
	"userID" : 1,
	"fromAccount" : 1,
	"toAccount" : 2,
	"amountToTransfer" : 500
}
```
### Response
```json
{
    "errorMessages": null,
    "successMsgs": [
        "Account Balance: 1000"
    ],
    "account": {
        "accountID": 1,
        "userID": 1,
        "currentBalance": 1000
    },
    "errorCode": "SUCCESS"
}
```

## Running and Testing the App
### Prerequisites
* JDK 8
* Apache Maven 3.3.9 and above
* Git 
* curl

```console
git clone https://github.com/magnusram05/moneytransfer.git

cd moneytransfer

mvn clean test

mvn exec:java
```
Ensure that the application is successfully running by checking the presence of following logs in the console window
```console
...
INFO: [HttpServer] Started.
Jersey app started with WADL available at http://localhost:8080/fintech/application.wadl
```

Once the app is running, test the Money Transfer and Balance inquiry APIs using ``curl`` or any REST API testing tool

### Testing using curl

Check account balance of ``Account-1: 1000`` and ``Account-2: 2000``
```console
curl -H "Content-Type: application/json" -X POST -d '{"userID":1,"accountID":1}' http://localhost:8080/fintech/account/v1/balance

curl -H "Content-Type: application/json" -X POST -d '{"userID":2,"accountID":2}' http://localhost:8080/fintech/account/v1/balance
```
Initiate money transfer (Transfer ``500`` from Account-1 to Account-2)
```console
curl -H "Content-Type: application/json" -X POST -d '{"userID":1,"fromAccount":1, "toAccount":2, "amountToTransfer":"500"}' http://localhost:8080/fintech/account/v1/moneytransfer
```
Check the balance again.  New balance should be ``Account-1: 500`` and ``Account-2: 2500``
```console
curl -H "Content-Type: application/json" -X POST -d '{"userID":1,"accountID":1}' http://localhost:8080/fintech/account/v1/balance

curl -H "Content-Type: application/json" -X POST -d '{"userID":2,"accountID":2}' http://localhost:8080/fintech/account/v1/balance
```
