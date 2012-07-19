Feature: Examples from the Euronext Market-to-Limit PDF

  Scenario: On a class of Securities traded by auction - Example 1
  Only Market to Limit orders in the order book
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 50       | MarketToLimit |       |
      | B      | Sell | 40       | MarketToLimit |       |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 40       | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 10       | Limit      | 10    |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded by auction - Example 2
  There are Market to limits, Pure Market orders and limited orders
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | MarketToLimit |       |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
      | D      | Sell | 10       | Limit         | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type    | Price |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded by auction - Example 3
  There are Market to limits and Pure Market orders
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | MarketToLimit |       |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
      | D      | Sell | 5        | Limit         | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 5        | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type    | Price |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
      | A      | Buy  | 5        | Limit         | 10    |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 1
  Only Market to Limit orders in the order book
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 50       | MarketToLimit |       |
      | B      | Sell | 40       | MarketToLimit |       |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 40       | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 10       | Limit      | 10    |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 2
  There are Market to limits and Pure Market orders
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | MarketToLimit |       |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
      | D      | Sell | 10       | Limit         | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | B      | Buy  | 10       | MarketOrder |       |
      | C      | Buy  | 10       | Limit       | 10    |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded on a continuous mode – Call phase - Example 3
  There are Market to limits, Pure Market orders and Limited orders
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | MarketToLimit |       |
      | B      | Buy  | 10       | MarketOrder   |       |
      | C      | Buy  | 10       | MarketToLimit |       |
      | D      | Sell | 5        | Limit         | 10    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 5        | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | B      | Buy  | 10       | MarketOrder |       |
      | A      | Buy  | 5        | Limit       | 10    |
      | C      | Buy  | 10       | Limit       | 10    |
    And "Sell" order book is empty

  Scenario: On a class of Securities traded on a continuous mode – Continuous phase - Example 1
  There are limit orders in the order book
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 15
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | Limit         | 15    |
      | B      | Buy  | 10       | Limit         | 12    |
      | C      | Buy  | 10       | Limit         | 10    |
      | D      | Sell | 25       | MarketToLimit | 15    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 10       | 15    |
