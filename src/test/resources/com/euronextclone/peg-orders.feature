Feature: Pegged orders
  @focus
  Scenario: New buy and sell order aaa
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 200      | Limit      | 10.5  |
      | B      | Buy  | 150      | Peg        | 10.5  |
      | B      | Buy  | 70       | Peg        | 10.5  |
      | B      | Buy  | 125      | Limit      | 10.5  |
      | C      | Sell | 150      | Limit      | 10.5  |
      | C      | Sell | 70       | Limit      | 10.5  |
      | D      | Sell | 125      | Limit      | 10.5  |
