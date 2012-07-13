Feature: Pegged orders

  Background:
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 200      | Limit      | 10.5  |
      | B      | Buy  | 150      | Peg        | 10.5  |
      | B      | Buy  | 70       | Peg        | 10.5  |
      | B      | Buy  | 125      | Limit      | 10.5  |
      | C      | Sell | 150      | Limit      | 10.9  |
      | C      | Sell | 70       | Limit      | 10.9  |
      | D      | Sell | 125      | Limit      | 11    |

  @focus
  Scenario: Example 1 Step 1
    Then remaining buy order book depth is 4
    And remaining sell order book depth is 3

  @focus
  Scenario: Example 1 Step 2
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price              |
      | E      | Buy  | 200      | Limit      | 10.800000000000001 |
    Then remaining buy order book depth is 5
    And remaining sell order book depth is 3
    And best limits are:
      | Side | Limit |
      | Buy  | 10,8  |
      | Sell | 10,9  |
