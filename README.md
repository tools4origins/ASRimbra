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

Download the dependencies and install apps with :
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
@todo : add infra presentation

Repositories
=> multiples

TokenManager

REST API

Spark (easy to multithread, to configure)
@todo : check number of thread used by Spark, should not be limited (most thread are waiting for network answer)

Static Web Client (static donc sympa en cache)

Nginx, cluster, load balancing en round robin

@todo: monitoring
@todo: remove useless gzip compression

## Understanding the code
#### Java 8
ASRimbra use Java 8's streams, lambdas, optional, etc. You may want to have a look at those concepts before going further.
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








