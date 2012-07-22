Feature: Examples from the Euronext Pure Market Order PDF

  Scenario: Call Phase - Example 1 - the Market order is totally filled
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 50       | MTL   |
      | B      | Buy  | 90       | 10.1  |
      | C      | Buy  | 10       | 9.9   |
      | D      | Sell | 40       | MTL   |
      | E      | Sell | 100      | 10.08 |
      | F      | Sell | 60       | 10.15 |
    Then the calculated IMP is:
      | 10.08 |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 20       | MO    |
    Then the calculated IMP is:
      | 10.1 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10.1  |
      | A             | E              | 10       | 10.1  |
      | G             | E              | 20       | 10.1  |
      | B             | E              | 70       | 10.1  |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 20       | 10.1  | 10.15 | 60       | F      |
      | C      | 10       | 9.9   |       |          |        |


  Scenario: Call Phase - Example 2 - the market order is partially filled
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MTL   |
      | D      | Sell | 45       | MO    |
    Then the calculated IMP is:
      | 10 |
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 20       | MO    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10    |
      | G             | D              | 5        | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | G      | 15       | MO    |       |          |        |


  Scenario: Call Phase - Example 3 - the Indicative Matching Price is higher than the best limit & equal to the reference price
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 40       | 9.98  |
    Then the calculated IMP is:
      | 10 |


  Scenario: Call Phase - Example 4 - the Indicative Matching Price is equal to the best limit & lower to the reference price Reference price
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 41       | 9.98  |
    Then the calculated IMP is:
      | 9.98 |


  Scenario: Call Phase - Example 5 - the Indicative Matching Price is equal to the best limit & higher to the reference price Reference price
  Broker G enters a sell Limit order at 10,02€ for the purchase of 40 shares
  The Indicative Matching Price adopted within the range [10,02;+∞] is 10.02€, since this is the closest to the reference
  price of 10€ and the only price allowing 80 shares to be traded.
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 40       | 10.02 |
    Then the calculated IMP is:
      | 10.02 |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 40       | MO    | 10.02 | 40       | G      |


  Scenario: Trading session Phase - Example 1 - the Market order is totally executed upon entry
  Broker C enters a Market order for the purchase of 110 shares
  The order is executed with 100 shares at 10,2€ and 10 shares at 10.3€ (in compliance with the
  collars).
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Buy  | 110      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 10       | 10.3  |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      |        |          |       | 10.3  | 50       | B      |


  Scenario: Trading session Phase - Example 2 - the Market order is partially executed upon entry
  Broker C enters a Market order for the purchase of 200 shares
  The order is partly executed with 100 shares at 10.2€ and 60 at 10.3€ (in compliance with the
  collars)
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Buy  | 200      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 60       | 10.3  |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | C      | 40       | MO    |       |          |        |


  Scenario: Trading session Phase - Example 3 - there are only Market orders in the order book, trade price is the last traded price
  Broker C enters a Market order for the sale of 170 shares
  The order is partly executed, with 100 shares at 10€ each, representing the reference price witch
  is the last price.
  Priority of execution for market orders on the bid side is based on the rule of “First Come, first
  served”. The un-executed portion of the ask market order, representing 70 shares, remains on the
  order book.
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 90       | MO    |
      | B      | Buy  | 10       | MO    |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 90       | MO    |       |          |        |
      | B      | 10       | MO    |       |          |        |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Sell | 170      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | C              | 90       | 10    |
      | B             | C              | 10       | 10    |


  Scenario: Trading session Phase - Example 4 - the best limit is higher that the last traded price
  Broker D enters a limit order at 10€ for the sale of 120 shares
  The order is executed, with 100 shares at 10.1€ (90 representing the Market Order and 10 the
  limit order at 10.1€) and 20 at 10.08€.
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 90       | MO    |
      | B      | Buy  | 10       | 10.1  |
      | C      | Buy  | 20       | 10.08 |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 90       | MO    |       |          |        |
      | B      | 10       | 10.1  |       |          |        |
      | C      | 20       | 10.08 |       |          |        |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | D      | Sell | 120      | 10    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 90       | 10.1  |
      | B             | D              | 10       | 10.1  |
      | C             | D              | 20       | 10.08 |
    And the book is empty


  Scenario: Trading session Phase - Example 5 - the best limit is lower that the last traded price
  Broker D enters a limit order at 10€ for the sale of 120 shares
  The order is executed, with 90 shares at 10.3€, 10 shares at 10.1€ and 20 at 10.08€.
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10.3
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 90       | MO    |
      | B      | Buy  | 10       | 10.1  |
      | C      | Buy  | 20       | 10.08 |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 90       | MO    |       |          |        |
      | B      | 10       | 10.1  |       |          |        |
      | C      | 20       | 10.08 |       |          |        |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | D      | Sell | 120      | 10    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 90       | 10.1  |
      | B             | D              | 10       | 10.1  |
      | C             | D              | 20       | 10.08 |
    And the book is empty
