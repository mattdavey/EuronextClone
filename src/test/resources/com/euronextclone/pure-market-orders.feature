Feature: Pure market orders

  @focus
  Scenario: Resting market order is partially filled by incoming limit order
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | MO    |
      | B      | Sell | 60       | 10.3  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 60       | 10.3  |
#    And "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type  | Price |
#      | A      | Buy  | 40       | MarketOrder |       |
    And "Sell" order book is empty

  Scenario: Trading Session Example 1
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
      | C      | Buy  | 110      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 10       | 10.3  |
    And "Buy" order book is empty
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | B      | Sell | 50       | Limit      | 10.3  |

  Scenario: Trading Session Example 2
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
      | C      | Buy  | 200      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 60       | 10.3  |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | C      | Buy  | 40       | MarketOrder |       |
    And "Sell" order book is empty
