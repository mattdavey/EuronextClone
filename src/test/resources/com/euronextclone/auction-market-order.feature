Feature: Auction Phase Matching Rules

  Background:
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And   that reference price is 10

  Scenario: Simplest single trade full match on offsetting limit orders
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 50       | 10    |
      | B      | Sell | 50       | 10    |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 50       | 10    |

  Scenario: Simplest single trade partial match on offsetting limit orders
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 30       | 10    |
      | B      | Sell | 50       | 10    |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 30       | 10    |

  Scenario: Simple multi trade full match on offsetting orders (multiple fills due to to sell side)
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 30       | 10    |
      | B      | Sell | 20       | 10    |
      | C      | Sell | 10       | 10    |
    When class auction completes
    Then the following trades are generated:
      | Buying Broker | Selling Broker | Quantity | Price |
      | A             | B              | 20       | 10    |
      | A             | C              | 10       | 10    |

  Scenario: Simple multi trade full match on offsetting orders (multiple fills due to to buy side)
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 30       | 10    |
      | B      | Buy  | 50       | 10    |
      | C      | Sell | 80       | 10    |
    When class auction completes
    Then the following trades are generated:
      | Buying Broker | Selling Broker | Quantity | Price |
      | A             | C              | 30       | 10    |
      | B             | C              | 50       | 10    |
