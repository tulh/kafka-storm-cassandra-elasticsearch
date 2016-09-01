# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary:
This project contains source code of the integration between apache kafka, storm and cassandra, spark. The data input came from social network hocavalam.
* Version:
1.0-SNAPSHOT

### How do I get set up? ###

* setup apache kafka server (multiple nodes)
* setup apache storm server (nimbus, zookeeper, storm)
* setup apache cassandra cluster (data center contains 3-4 nodes)
* Deployment instructions:
1. to run in localmode: storm jar <jar package> <main class to execute>.
2. to run in production mode (distributed) storm jar <jar package> <main class to execute> args... (args[0] is name of the topology)

### Contribution guidelines ###

* sample module contains example source code & test for multiple use cases
* storm-integration and other modules contains the business and being used to create jar files

### Who do I talk to? ###
tuleha52@gmail.com
