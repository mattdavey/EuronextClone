Feature: Examples from the Euronext the Peg Orders PDF

  Background:
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"

#  @focus
  Scenario: Example 1
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 200      | 10.5  |
      | B      | Buy  | 150      | Peg   |
      | B      | Buy  | 70       | Peg   |
      | B      | Buy  | 125      | 10.5  |
      | C      | Sell | 130      | 10.9  |
      | C      | Sell | 350      | 10.9  |
      | D      | Sell | 275      | 11    |
#    Then "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type | Price |
#      | A      | Buy  | 200      | Limit      | 10.5  |
#      | B      | Buy  | 150      | Peg        | 10.5  |
#      | B      | Buy  | 70       | Peg        | 10.5  |
#      | B      | Buy  | 125      | Limit      | 10.5  |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 130      | Limit      | 10.9  |
      | C      | Sell | 350      | Limit      | 10.9  |
      | D      | Sell | 275      | Limit      | 11    |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | E      | Buy  | 200      | 10.8  |
    Then no trades should have been generated
#    And "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type | Price |
#      | E      | Buy  | 200      | Limit      | 10.8  |
#      | B      | Buy  | 150      | Peg        | 10.8  |
#      | B      | Buy  | 70       | Peg        | 10.8  |
#      | A      | Buy  | 200      | Limit      | 10.5  |
#      | B      | Buy  | 125      | Limit      | 10.5  |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 130      | Limit      | 10.9  |
      | C      | Sell | 350      | Limit      | 10.9  |
      | D      | Sell | 275      | Limit      | 11    |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 100      | 10.9  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | G             | C              | 100      | 10.9  |
#    And "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type | Price |
#      | E      | Buy  | 200      | Limit      | 10.8  |
#      | B      | Buy  | 150      | Peg        | 10.8  |
#      | B      | Buy  | 70       | Peg        | 10.8  |
#      | A      | Buy  | 200      | Limit      | 10.5  |
#      | B      | Buy  | 125      | Limit      | 10.5  |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 30       | Limit      | 10.9  |
      | C      | Sell | 350      | Limit      | 10.9  |
      | D      | Sell | 275      | Limit      | 11    |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Sell | 250      | 10.8  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | E             | G              | 200      | 10.8  |
      | B             | G              | 50       | 10.8  |
#    And "Buy" order book should look like:
#      | Broker | Side | Quantity | Order Type | Price |
#      | A      | Buy  | 200      | Limit      | 10.5  |
#      | B      | Buy  | 125      | Limit      | 10.5  |
#      | B      | Buy  | 100      | Peg        | 10.5  |
#      | B      | Buy  | 70       | Peg        | 10.5  |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 30       | Limit      | 10.9  |
      | C      | Sell | 350      | Limit      | 10.9  |
      | D      | Sell | 275      | Limit      | 11    |
