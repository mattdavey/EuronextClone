Feature: Pegged orders with limit

  @focus
  Scenario: Peg Order Limit Fill Trade
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type   | Price | Limit |
      | A      | Buy  | 200      | Limit        | 11.5  |       |
      | B      | Buy  | 150      | PegWithLimit | 11.5  | 11.6  |
      | C      | Sell | 200      | Limit        | 11.5  |       |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | C              | 200      | 11.5  |
    And remaining buy order book depth is 0
    And remaining sell order book depth is 0
