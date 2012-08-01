# EuronextClone


Inspired from Price-Time Matching [Engine](https://gist.github.com/2855852) based on Euronext documentation publically available:

[The Peg Orders](http://www.euronext.com/fic/000/041/609/416094.pdf)

[Pure Market Order](http://www3.production.euronext.com/fic/000/041/480/414808.pdf)

[Auction](http://www.nyse.com/pdfs/5653_NYSEArca_Auctions.pdf)

[The Market to limit order](http://www3.production.euronext.com/fic/000/041/480/414806.pdf)

[Trading](https://europeanequities.nyx.com/en/trading/continuous-trading-process)

[The Stop Order](http://www3.production.euronext.com/fic/000/041/480/414809.pdf)

[Stop Order](http://www.euronext.com/fic/000/010/550/105509.pdf)

[IMP/TOP calculation](http://www.asx.com.au/products/calculate-open-close-prices.htm)

## Simple architecture Proof Of Concept (PoC)

![Basic](https://github.com/mattdavey/EuronextClone/raw/master/assets/basic.jpg)

## Rapid Failover based on multiple copies and heart-beating
The architecture below is based on the [LMAX](http://martinfowler.com/articles/lmax.html) architecture, but leveraging ZeroMQ instead of [Informatica](http://www.informatica.com/us/products/messaging/) Ultra Messaging
![Basic](https://github.com/mattdavey/EuronextClone/raw/master/assets/complex.jpg).  Further reading available [here](http://mdavey.wordpress.com/2012/08/01/financial-messaging-zeromq-random-reading/)
