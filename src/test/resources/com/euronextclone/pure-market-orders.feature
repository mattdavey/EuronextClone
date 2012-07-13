Feature: Pure market orders

  Background:
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Sell | 100      | Limit      | 10.2  |
      | B      | Sell | 60       | Limit      | 10.3  |

  Scenario: Trading Session Example
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Buy  | 100      | Limit      | 10.2  |
    Then remaining buy order book depth is 4
    And remaining sell order book depth is 3

    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | G             | C              | 100      | 10.9  |
