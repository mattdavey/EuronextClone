# Setting up development environment

## Prerequisites
The only prerequisite required is that your system has [0MQ library](http://www.zeromq.org/) built and installed. 0MQ is a native dependecy used for messaging and is not automatically resolved by Maven build.

Detailed instructions on how to build and install 0MQ on your system are available on [ZeroMQ site](http://www.zeromq.org/intro:get-the-software).

## Build
First use [git](http://git-scm.com/) to clone this repo:

    git clone https://github.com/mattdavey/EuronextClone.git
    cd EuronextClone

EuronextClone is built with [Maven](http://maven.apache.org/).

    mvn clean install

The above will build the prototype and run all unit tests (cucumber-jvm over JUnit runner).

## Run

There are currently two application available in the prototype: [FIX Server](#fix-server) and [FIX client](#fix-client). You can run a sinle copy of FIX server and one or more copies of FIX Client.
This is changing soon though, so check this page often. We are moving to architecture where multiple FIX Servers may run, and we are moving to architecture where matching engine (ME)
is a separate from FIX Server, and you can run multiple copies of those too.

### FIX Server

FIX Server entry point is located at [FixServerApp](../src/main/java/com/euronextclone/fix/server/FixServerApp.java). FIX Server is preconfigured to accept connections from BROKER-A and BROKER-B clients.
(See [FixServer.cfg](../src/main/resources/FixServer.cfg))

To run FIX Server using Maven:

    mvn exec:exec -Prun-fix-server

This assumes that native 0MQ library is available at **/usr/local/lib** unless you have overriden its location using **zmq.library.path** environment variable.

If you are running FIX Server manually or from your favorite IDE make sure that **-Djava.library.path=...** is set to the location where you have installed 0MQ library to.

### FIX Client

FIX Client entry point is located at [FixClientApp](../src/main/java/com/euronextclone/fix/client/FixClientApp.java).

To run FIX Client using Maven (as broker A):

    mvn exec:exec -Prun-fix-client-a

To run FIX Client using Maven (as broker B):

    mvn exec:exec -Prun-fix-client-b

If you running FIX Client manually or from your favorite IDE then you can pass the following command line arguments:

 **--broker=BROKER**

 The broker argument configures the FIX client for a specific broker configuration.
 For example, passing **--broker=A** uses [FixBrokerA.cfg](../src/main/resources/FixBrokerA.cfg) configuration. And passing **--broker=B** uses [FixBrokerB.cfg](../src/main/resources/FixBrokerB.cfg) configuration.

 You can start multiple copies of FIX Client, with different **"--broker"** configured. When the FIX client starts it is ready to accept user's input.

 * typing "**q**" terminates the FIX Client
 * typing "**h**" lists all available commands

The following commands are currently supported:

#### Place Limit Order

    buy MSFT 10@34

#### Place Market Order

    sell MSFT 5
