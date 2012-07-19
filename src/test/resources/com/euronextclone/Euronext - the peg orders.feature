Feature: Examples from the Euronext the Peg Orders PDF

  Background:
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"

  @focus
  Scenario: Example 1
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 200      | Limit      | 10.5  |
      | B      | Buy  | 150      | Peg        |       |
      | B      | Buy  | 70       | Peg        |       |
      | B      | Buy  | 125      | Limit      | 10.5  |
      | C      | Sell | 130      | Limit      | 10.9  |
      | C      | Sell | 350      | Limit      | 10.9  |
      | D      | Sell | 275      | Limit      | 11    |
#    Then "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type | Price |
#      | A      | Buy  | 200      | Limit      | 10.5  |
#      | B      | Buy  | 150      | Peg        | 10.5  |
#      | B      | Buy  | 70       | Peg        | 10.5  |
#      | B      | Buy  | 125      | Limit      | 10.5  |
