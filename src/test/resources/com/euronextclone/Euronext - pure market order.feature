Feature: Examples from the Euronext Pure Market Order PDF

  @focus
  Scenario: Call Phase - Example 1
  The Market order is totally filled
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 50       | MarketToLimit |       |
      | B      | Buy  | 90       | Limit         | 10.1  |
      | C      | Buy  | 10       | Limit         | 9.9   |
      | D      | Sell | 40       | MarketToLimit |       |
      | E      | Sell | 100      | Limit         | 10.08 |
      | F      | Sell | 60       | Limit         | 10.15 |
    Then the calculated IMP is:
      | 10.08 |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type    | Price |
      | G      | Buy  | 20       | MarketOrder   |       |
    Then the calculated IMP is:
      | 10.1 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10.1  |
      | A             | E              | 10       | 10.1  |
      | G             | E              | 20       | 10.1  |
      | B             | E              | 70       | 10.1  |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | B      | Buy  | 20       | Limit      | 10.1  |
      | C      | Buy  | 10       | Limit      | 9.9   |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | F      | Sell | 60       | Limit      | 10.15 |
