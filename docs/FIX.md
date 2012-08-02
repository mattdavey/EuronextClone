## FIX Server

FIX Server entry point is located at [FixServerApp](../src/main/java/com/euronext/fix/server/FixServerApp.java). FIX Server is preconfigured to accept connections from BROKER-A and BROKER-B clients.
(See [FixServer.cfg](../src/main/resources/FixServer.cfg))

## FIX Client

FIX Client entry point is located at [FixClientApp](../src/main/java/com/euronext/fix/client/FixClientApp.java).
FIX Client currently supports the following command line arguments:

 **--broker=BROKER**

 The broker argument configures the FIX client for a specific broker configuration.
 For example, passing **--broker=A** uses [FixBrokerA.cfg](../src/main/resources/FixBrokerA.cfg) configuration. And passing **--broker=B** uses [FixBrokerB.cfg](../src/main/resources/FixBrokerB.cfg) configuration.

 You can start multiple copies of FIX Client, with different **"--broker"** configured. When the FIX client starts it is ready to accept user's input.

 * typing "q" terminates the FIX Client
 * typing "h" lists all available commands

The following commands are currently supported:

* Place Limit Order (Example: **buy MSFT 10@34**)
* Place Market Order (Example: **sell MSFT 5**)