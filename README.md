# Registration and Authentication Service  
## Introduction  
This is a web service that registers and authenticates users.  
The main technologies used here are :
*  Java Servlet Technology 
* JAX-RS REST API (Jersey)
* MongoDB (as persistence)  
* JUnit (unit testing)

## Mode of Operation  
### User registration  
Users are required to fill up a form which contains the following fields:  
* Username (unique)
* Password
* Confirm Password 
* First Name
* Middle Name (Optional)
* Last Name
* A Security question 
* Answer to the security question 

On sending the form to the server, validation occurs and incorrect parameters if provided results in HTTP response 
code 406. If passwords don't match then a 401 response code is sent. Success is 200 as usual. And upon successful 
server-side validation, the data is persisted on a MongoDB Atlas cluster.  

### User authentication 
Users are authenticated after submission of a form containing their username and password. Success is code 200 and 
failure is code 401.  

### Updating of user details 
A user can change any of their user detail in any order. Success is 200 and 406 if the user does not exist. For all 
other errors a code 500 is shown.  

### Updating unique user ID  
Each user is identified by a unique user ID which is a combination of 
[UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#randomUUID--), [Hex String of System.nanoTime()](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#nanoTime--) and the username 
itself. It can only be viewed on the client but has to passed as a URL path parameter. Success is 200 as usual, failure is 406. 

## Possible Bugs 
* Changing the unique user ID may result in duplicate inserts on username. [See this SO thread](https://stackoverflow.com/questions/4012855/how-update-the-id-of-one-mongodb-document#comment84079190_4012997) thread for more details.

* Communication with Atlas cluster takes long time.  

Status - **Incomplete**


