Feature: Auction Phase Matching Rules

  Background:
    Given that reference price is 10

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
