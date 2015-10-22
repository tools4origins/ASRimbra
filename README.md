# ASRimbra

A distributed and easily scalable application to exchange message with other user

Project made for a scholar microprojet

## Download
> `cd folder/where/you/want/to/download/ASRimbra`
>
> `git init`
>
> `git remote add origin git@bitbucket.org:pandawan/asrimbra.git`
>
> `git pull`

## Configuration

@todo : add info / token, db, repo, (cluster ?)

## Installation
You need to have maven and Java JDK 1.8+ installed on your computer first.

Download the dependencies and install apps with maven :
> `mvn install`

To package DirectoryManager use, *in its subfolder*:

> `mvn package`

This generated target/ASRimbra-DirectoryManager-{version}-jar-with-dependencies.jar that you can then move and use wherever you like.

To package MailboxManager use, *in its subfolder*:
> `mvn package`

This generated target/ASRimbra-MailboxManager-{version}-jar-with-dependencies.jar that you can then move and use wherever you like.

You can launch both managers *if you did not move them* using *in their subfolders*
:
> `mvn exec:java`

or directly with java, wherever you want:
> `cd somewhere/`
>
> `java -jar {name of the jar}.jar`
@todo : fix this
## Infrastructure

We have a nice infrastructure ! It may be slightly more evolved that what was requested,
have a look to ./ASRimbra_architecture.png :-).

### Clients
Both ManagerClient and AdministrationClient are web clients. They can be resume to a single static web page having forms
used to contact the server-side REST API.

This has several advantages :
* It's easy to develop, test and maintain (click on a button to create a request,
the browser will soon remember your parameters).
* It's easy to send on other devices:
    * It works in local without dependencies
    * It works on a server.
    * It is static ! Nothing is easier to cache.
    * It works if it is saved from a server (Ctrl + S on the page and TADA ! You have really save the client,
    not just a generated file without any logic)
    * It is easy to create an software from it : it just need to be wrapped in a WebView
    * It is easy to create a mobile application from it : it just need to be wrapped in a WebView (eg : with Cordova)
    * It works with a REST API, and a REST API make it easier to interconnect ASRimbra with other services.
    * Even in command line, you can use it through lynx-like browser.
* Browser and WebView in general have no problems to store cookies, and as next section explain, ASRimbra use cookies.

Server complies with browser particular demands on REST API related to Cross Origin Resource Sharing (CORS):
Theirs responses will have the request origin, and they provide an Option method where there is a non-GET/POST
method available.

### Communication and authentication
As said above, ASRimbra use a REST API to communicate with server.

#### Authentication with token

Authentication is managed through a Token.

This token is of the form username/expiration date/signature

Signature is a hash of the username, the expiration date and a key hard coded in all the servers.

This means that once you have your token, any server can checked if you are connected with a simple
comparison between the signature of your token and what they calculate from your username, expirationDate and this key.

No sessions are need to perform this check, so the server is fully stateless. **This mean easy scalability.**

No database access are need to perform the check, we have a statelessness at a very low cost.

One inconvenient is that you cannot revoke token, you can only destroy cookie and if someone save the value of it,
he can regenerated it and continue to browse even if its account was destroyed until the cookie die. We do not believe
it is a problem with a time to live for the cookie of 1 hour.

#### REST API

DirectoryManager base URI is `http://server:7654`

Following routes are supported by DirectoryManager :

 > POST "/connect" — Return a token (after identity check) in a cookie — Query parameters are "mail" and "password"
 >
 > GET "/user/getAll" — List all users
 >
 > POST "/user/add" — Add a user from a mail and a password — Query parameters are "mail", "password"
 >
 > OPTIONS "/user/removeByMail/:mail" — Return the available options for this route
 >
 > DELETE "/user/removeByMail/:mail" — Delete a user with the mail :mail.
 >
 > GET "/user/getRight" — get the rights of user which mail is mail. — Query parameter is "mail"
 >
 > POST "/user/setAdmin" — set Admin the user which mail is mail. — Query parameter is "mail"
 >
 > POST "/user/setSimpleUser" — set Simple User the user which mail is mail. — Query parameter is "mail"
 >
 > GET "/user/getByMail" — return data about the user which mail is mail. — Query parameter is "mail"
 >
 > GET "/user/getRoleByCredentials" — return a  Simple User the user which mail is mail. — Query parameter is "mail" "login", "password"
 >
 > GET "/disconnect" — destroy token in your cookie.

If the route is prefixed by user/, then you have to be authenticated as an Admin to access it.

Following routes are supported by MailboxManager :

MailboxManager base url is `server:4567`

> POST "/connect" — Return a token (after identity check) in a cookie — Query parameters are "mail" and "password"
>
> GET "/disconnect" — destroy token in your cookie.
>
> GET "/mailbox/" — List all the messages associated to you (identity retrieved from token)
>
> GET "/mailbox/:id" — Display message which id is :id if it is associated to you (identity retrieved from token).
>
> DELETE "/mailbox/:id" — Remove message which id is :id if it is associated to you (identity retrieved from token). *
>
> POST "/mailbox/send/" — Send a message from your account to to with title title and content content (identity
 retrieved from token). — Query parameters are "to" "title" and "content"

If the route is prefixed by user/, then you have to be authenticated to access it.

*: This mean that both the sender and the receiver can delete an information of our servers. It is a choice, both should be
able to remove what they have written/receive from our servers.

### Servers
Both DirectoryManager and MailboxManager are build on Spark.

It allows us to create complex application with minimal syntax.

Spark server are**multithreaded**and easy to configure.

### Entities and persistence

There are 4 entities in ASRimbra:
* User
* Mail
* Role — use to identify admins from simple users (a simple Enum).
* MailingList — use for instance for newsletters.

User, Mail and MailingList are managed with repositories.

One of the biggest constraint of the application is that there is no direct access from MailboxManager to User DB and no
direct access from MailboxManager to Mail( and MailingList) DB.

* MemoryRepositories does not allow that and does not provide durability, thus they are no longer used but where usefull
during development and may be usefull again in certains case (eg tests).
* JPARepositories allow access to a DB with hibernate
* RemoteRepositories allow access to a distant server which has to implement a corresponding REST API. It is used for
the communications from MailboxManager to DirectoryManager.

###

@todo : reload javadoc
@todo : check number of thread used by Spark, should not be limited (most thread are waiting for network answer)

Testé

Nginx, cluster, load balancing en round robin

@todo: monitoring
@todo: remove useless gzip compression

## Understanding the code
#### Java 8
ASRimbra use Java 8's streams, lambdas, optional, etc. You may want to have a look at those concepts
before going further.
* Streams: http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
* Lambda expression: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
* Optional: http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html

#### Spark Framework
ASRimbra use Spark Framework for its REST API.

Spark Framework is a java web framework and has nothing to do with Apache Spark which is a computing framework

To be able to understand the code, you may want to read Spark Framework documentation:
* http://sparkjava.com/documentation.html

#### Hibernate

ASRimbra use hibernate to communicate with databases (default behaviour)

Information are defined in core/src/main/resources/hibernate*.cfg.xml

* Tutorial for using hibernate and POJO: http://www.tutorialspoint.com/hibernate/index.htm

@todo: move all config to ASRimbra/pom.xml
#### DavidWebb

ASRimbra use DavidWebb, a lightweight Java HTTP-Client for calling JSON REST-Services.

To be able to understand the code, you may want to discover DavidWebb repository:
* https://github.com/hgoebl/DavidWebb

#### PBKDF2

ASRimbra use a PBKDF2 by rtner.de to calculate hash of sensitive information. More on

* More on PBKDF2: https://en.wikipedia.org/wiki/PBKDF2
* More on this implementation: http://www.rtner.de/software/PBKDF2.html








