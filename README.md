# Euronext Clone
Inspired by github Price-Time Matching [Engine](https://gist.github.com/2855852), based on Euronext documentation publically available, and the madness of too many years in finance:

*   [The Peg Orders](http://www.euronext.com/fic/000/041/609/416094.pdf)
*   [Pure Market Order](http://www3.production.euronext.com/fic/000/041/480/414808.pdf)
*   [Auction](http://www.nyse.com/pdfs/5653_NYSEArca_Auctions.pdf)
*   [The Market to limit order](http://www3.production.euronext.com/fic/000/041/480/414806.pdf)
*   [Trading](https://europeanequities.nyx.com/en/trading/continuous-trading-process)
*   [The Stop Order](http://www3.production.euronext.com/fic/000/041/480/414809.pdf)
*   [Stop Order](http://www.euronext.com/fic/000/010/550/105509.pdf)
*   [IMP/TOP calculation](http://www.asx.com.au/products/calculate-open-close-prices.htm)

## Quick Links
[Setting up dev environment](https://github.com/mattdavey/EuronextClone/blob/master/docs/SettingUpDevEnvironment.md)

[FIX to matching eninge flows over 0MQ](https://github.com/mattdavey/EuronextClone/blob/master/docs/Fix%20to%20Matching%20Cluster%20Architecture.md)

## History
The project initially started in the distant past as a Proof Of Concept (PoC) (see below) mainly aimed at attempting to model the data flows of a matching server in Java.

Over time, work and thought has been put in to add ancillary services around the core order book with the aim of moving towards the high level architecture detailed below, offering Master/Slave with heart-beating from a resiliance perspective.

It is hoped that at some point in the future performance numbers can be provided on suitable hardware to allow appropriate tuning and improve the architecture from a latency perspective.

## Proof Of Concept (PoC)
Primarily this PoC architecture is aimed at joining all the dots together to ensure a client can submit an FIX order, and receive appropriate ExecutionReports

![Basic](https://github.com/mattdavey/EuronextClone/raw/master/assets/basic.jpg)

## High Level Architecture
The architecture below is based on the [LMAX](http://martinfowler.com/articles/lmax.html) architecture, but leveraging ZeroMQ instead of [Informatica](http://www.informatica.com/us/products/messaging/) Ultra Messaging. Further reading available [here](http://mdavey.wordpress.com/2012/08/01/financial-messaging-zeromq-random-reading/)

![Basic](https://github.com/mattdavey/EuronextClone/raw/master/assets/complex.jpg).
