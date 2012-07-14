Feature: Basic order book construction

  @focus
  Scenario: Limit buy orders are sorted from high to low
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 100      | Limit       | 10.2  |
      | B      | Buy  | 100      | Limit       | 10.3  |
      | C      | Buy  | 100      | Limit       | 10.1  |
      | D      | Buy  | 100      | Limit       | 10.5  |
    Then "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | D      | Buy  | 100      | Limit       | 10.5  |
      | B      | Buy  | 100      | Limit       | 10.3  |
      | A      | Buy  | 100      | Limit       | 10.2  |
      | C      | Buy  | 100      | Limit       | 10.1  |
