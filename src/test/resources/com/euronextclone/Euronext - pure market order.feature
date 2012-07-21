Feature: Examples from the Euronext Pure Market Order PDF

  Scenario: Call Phase - Example 1
  The Market order is totally filled
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 50       | MTL   |
      | B      | Buy  | 90       | 10.1  |
      | C      | Buy  | 10       | 9.9   |
      | D      | Sell | 40       | MTL   |
      | E      | Sell | 100      | 10.08 |
      | F      | Sell | 60       | 10.15 |
    Then the calculated IMP is:
      | 10.08 |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 20       | MO    |
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


  Scenario: Call Phase - Example 2
  The market order is partially filled
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MTL   |
      | D      | Sell | 45       | MO    |
    Then the calculated IMP is:
      | 10 |
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 20       | MO    |
    Then the calculated IMP is:
      | 10 |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10    |
      | G             | D              | 5        | 10    |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | G      | Buy  | 15       | MarketOrder |       |
    And "Sell" order book is empty


  Scenario: Call Phase - Example 3
  The Indicative Matching Price is higher than the best limit & equal to the reference price
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 40       | 9.98  |
    Then the calculated IMP is:
      | 10 |


  Scenario: Call Phase - Example 4
  The Indicative Matching Price is equal to the best limit & lower to the reference price Reference price
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 41       | 9.98  |
    Then the calculated IMP is:
      | 9.98 |


  Scenario: Call Phase - Example 5
  The Indicative Matching Price is equal to the best limit & higher to the reference price Reference price
    Given that trading mode for security is "Continuous" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | MO    |
      | G      | Sell | 40       | 10.02 |
    Then the calculated IMP is:
      | 10.02 |


  Scenario: Trading session Phase - Example 1
  The Market order is totally executed upon entry
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Buy  | 110      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 10       | 10.3  |
    And "Buy" order book is empty
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | B      | Sell | 50       | Limit      | 10.3  |


  Scenario: Trading session Phase - Example 2
  The Market order is partially executed upon entry
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 60       | 10.3  |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Buy  | 200      | MO    |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | C             | A              | 100      | 10.2  |
      | C             | B              | 60       | 10.3  |
    And "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type  | Price |
      | C      | Buy  | 40       | MarketOrder |       |
    And "Sell" order book is empty


#  @focus

  Scenario: Trading session Phase - Example 3
  There are only Market orders in the order book, trade price is the last traded price
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 90       | MO    |
      | B      | Buy  | 10       | MO    |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | C      | Sell | 170      | MO    |
#    Then the following trades are generated:
#      | Buying broker | Selling broker | Quantity | Price |
#      | A             | C              | 90       | 10    |
#      | B             | C              | 10       | 10    |
