Feature: Market-to-limit orders

  Scenario: Continuous Phase Limit Orders
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | 15    |
      | B      | Buy  | 10       | 12    |
      | C      | Buy  | 10       | 10    |
      | D      | Sell | 25       | MTL   |
    Then remaining buy order book depth is 2
    And remaining sell order book depth is 1

  Scenario: Continuous Phase Limit Orders And Pure Market Order
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 10       | MO    |
      | B      | Buy  | 10       | 12    |
      | C      | Buy  | 10       | 10    |
      | D      | Sell | 25       | MTL   |
    Then remaining buy order book depth is 1
    And remaining sell order book depth is 1
