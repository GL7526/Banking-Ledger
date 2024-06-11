# Banking-Ledger

## Functionality

This application is used to keep track of deposits and withdrawals for various users

Some requirements for requests for the service are:
- all loads have a DebitCredit value of CREDIT and all authorizations have a DebitCredit value of DEBIT
- currency used is the same denomination throughout
- each load or authorization value must have a non-negative amount, if specified
- if a user does not have sufficient funds, the withdrawal will not go through

An additional function of the service is:
- when an authorization is declined and is followed by a load without a specified amount, then the load's amount will have a value equal to that of the declined authorization's, and will be added to the declined account's balance

This simulates completing a transaction by adding funds from any source, regardless of user. This means that another user can help complete the transaction if necessary.

The format for [requests](src/main/java/com/Ledger/components/requestBodies) to make deposits and withdrawals can be found in src > main > java > com > Ledger > components > requestBodies

[//]: # (This assumption comes from the provided [sample_tests]&#40;src/test/java/resources/sample_tests&#41;. We can see that after a declined authorization, the amount is added to the account, even though the user is different for the following load:)

[//]: # (![sample_tests_assumption]&#40;/images/SampleTestsAssmption.png&#41;)

[//]: # ()
[//]: # (This will only work if the load does not specify an amount and immediately follows a normally declined authorization.)

[//]: # ()
[//]: # (Since it also does not originally specify a load amount, the transaction amount is left out/is null on purpose in the event when tracked.)

## How to run this server locally

### Running using your IDE (IntelliJ)
Right click [LedgerApplication](src/main/java/com/Ledger/LedgerApplication.java) under Ledger > src > main > java > com.Ledger > LedgerApplication, and then click "Run":


![Run RunApplication](/images/RunApplication.png)

### Running through your CLI

- If you have java and maven installed and set up, you can also open your CLI
- Set your path to the path of the project 
- Run the command "mvn clean install" to compile the application
- Run "java -jar ./target/Ledger-0.0.1.jar"

### Running using Docker

You can also run this application using Docker

- Start the daemon
- Open your CLI
- Set your path to the path of the project
- Run "docker build -t ledgerapp ." to build the image
- Run "docker run ledgerapp"

The port is set to 8080, so you can send requests locally to http://localhost:8080/
<br>
An example is shown below:
<br>

![Load request example](/images/LoadExample.png)


You can edit the port number in the [application.properties](/src/main/resources/application.properties) file

Tests are located in [src/test/java](src/test/java)