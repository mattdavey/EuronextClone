Feature: Auction Phase Matching Rules

  Background: Given that market is in pre-opening phase

  Scenario: Simplest single trade full match on offsetting limit orders
    Given the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 50           | 10        | 10        | 50           | 2           | B          |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 50       | 10    |

  Scenario: Simplest single trade partial match on offsetting limit orders
    Given the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 30           | 10        | 10        | 50           | 2           | B          |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 30       | 10    |

  Scenario: Simple multi trade full match on offsetting orders (multiple fills due to to sell side)
    Given the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 30           | 10        | 10        | 20           | 2           | B          |
      |            |             |              |           | 10        | 10           | 3           | C          |
    When class auction completes
    Then the following trades are generated:
      | Buying Broker | Selling Broker | Quantity | Price |
      | A             | B              | 20       | 10    |
      | A             | C              | 10       | 10    |

  Scenario: Simple multi trade full match on offsetting orders (multiple fills due to to buy side)
    Given the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 30           | 10        | 10        | 80           | 3           | C          |
      | B          | 2           | 50           | 10        |           |              |             |            |
    When class auction completes
    Then the following trades are generated:
      | Buying Broker | Selling Broker | Quantity | Price |
      | A             | C              | 30       | 10    |
      | B             | C              | 50       | 10    |

  Scenario: the market order is totally filled
    Given that reference price is 10.0
    And the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 50           | MTL       | MTL       | 40           | 4           | D          |
      | G          | 7           | 20           | MO        | 10.08     | 100          | 5           | E          |
      | B          | 2           | 90           | 10.1      | 10.15     | 60           | 6           | F          |
      | C          | 3           | 10           | 9.9       |           |              |             |            |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10.1  |
      | A             | E              | 10       | 10.1  |
      | G             | E              | 20       | 10.1  |
      | B             | E              | 70       | 10.1  |
    And the book looks like:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | B          | 2           | 20           | 10.1      | 10,15     | 60           | 6           | F          |
      | C          | 3           | 10           | 9.9       |           |              |             |            |

  Scenario: the market order is partially filled
    Given that reference price is 10
    And the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 40           | MTL       | MO        | 45           | 2           | D          |
      | G          | 3           | 20           | MO        |           |              |             |            |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10    |
      | G             | D              | 5        | 10    |
    And the book looks like:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | G          | 3           | 15           | MO        |           |              |             |            |