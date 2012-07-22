Feature: Examples from the Euronext Market-to-Limit PDF

  Scenario: On a class of Securities traded by auction - Example 1 - Only Market to Limit orders in the order book
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 50       | MTL   |
      | B      | Sell | 40       | MTL   |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 40       | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 10       | 10    |       |          |        |


  Scenario: On a class of Securities traded by auction - Example 2 - There are Market to limits, Pure Market orders and limited orders
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MTL   |
      | B      | Buy  | 10       | MO    |
      | C      | Buy  | 10       | MTL   |
      | D      | Sell | 10       | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 10       | MO    |       |          |        |
      | C      | 10       | MTL   |       |          |        |


  Scenario: On a class of Securities traded by auction - Example 3 - There are Market to limits and Pure Market orders
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MTL   |
      | B      | Buy  | 10       | MO    |
      | C      | Buy  | 10       | MTL   |
      | D      | Sell | 5        | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 5        | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 10       | MO    |       |          |        |
      | C      | 10       | MTL   |       |          |        |
      | A      | 5        | 10    |       |          |        |


  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 1 - Only Market to Limit orders in the order book
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 50       | MTL   |
      | B      | Sell | 40       | MTL   |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 40       | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 10       | 10    |       |          |        |


  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 2 - There are Market to limits and Pure Market orders
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MTL   |
      | B      | Buy  | 10       | MO    |
      | C      | Buy  | 10       | MTL   |
      | D      | Sell | 10       | 10    |
    Then the calculated IMP is:
      | 10 |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 10       | MTL   | 10    | 10       | D      |
      | B      | 10       | MO    |       |          |        |
      | C      | 10       | MTL   |       |          |        |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 10       | MO    |       |          |        |
      | C      | 10       | 10    |       |          |        |


  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 3 - There are Market to limits, Pure Market orders and Limited orders
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MTL   |
      | B      | Buy  | 10       | MO    |
      | C      | Buy  | 10       | MTL   |
      | D      | Sell | 5        | 10    |
    Then the calculated IMP is:
      | 10 |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 10       | MTL   | 10    | 5        | D      |
      | B      | 10       | MO    |       |          |        |
      | C      | 10       | MTL   |       |          |        |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 5        | 10    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 10       | MO    |       |          |        |
      | A      | 5        | 10    |       |          |        |
      | C      | 10       | 10    |       |          |        |


  Scenario: On a class of Securities traded on a continuous mode – Continuous phase - Example 1
  There are limit orders in the order book
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 15
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | 15    |
      | B      | Buy  | 10       | 12    |
      | C      | Buy  | 10       | 10    |
      | D      | Sell | 25       | MTL   |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 15    |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | B      | 10       | 12    | 15    | 15       | D      |
      | C      | 10       | 10    |       |          |        |


  Scenario: On a class of Securities traded on a continuous mode – Continuous phase - Example 2
  There are limit orders and Pure Market orders in the order book
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 12
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MO    |
      | B      | Buy  | 10       | 12    |
      | C      | Buy  | 10       | 9     |
      | D      | Sell | 25       | MTL   |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | C      | 10       | 9     | 12    | 5        | D      |
