Feature: Market-to-limit orders

  @focus
  Scenario: Continuous Phase Limit Orders
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 10       | Limit         | 15    |
      | B      | Buy  | 10       | Limit         | 12    |
      | C      | Buy  | 10       | Limit         | 10    |
      | D      | Sell | 25       | MarketToLimit |       |
    Then remaining buy order book depth is 2
    And remaining sell order book depth is 1
