# FIX to Matching Engine Cluster Architecture

## Notes
* The following architecture diagram does not capture high availability and/or failover aspect of the overall design; only the main messaging flow is presented
* FIX server communicates with the clients over standard FIX protocol (clients not shown on the diagram)
* Each FIX server is responsoble for a subset of clients (brokers)
    * When orders come over FIX from those brokers they are published using multicast with the instrument name as the topic (routing key)
    * Each FIX server subscribes for execution updates related to its subset of brokers and relays them over FIX
* Each matching engine (ME) is responsible for its range of instruments (maybe configured statically or dynamically)
    * Each matching engine subscribes for order events related to its range of instruments on a multicast channel, published to by the FIX servers
    * When trades and other events are generated they are published using multicast with the broker as the topci (routing key)

## Diagram
![FIX to Matching Engine Flows over 0MQ](https://github.com/mattdavey/EuronextClone/raw/master/assets/FIXToMatchingEngineFlows0MQ.png)