Feature: Pegged orders

  Background:
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 200      | Limit      | 10.5  |
      | B      | Buy  | 150      | Peg        |       |
      | B      | Buy  | 70       | Peg        |       |
      | B      | Buy  | 125      | Limit      | 10.5  |
      | C      | Sell | 150      | Limit      | 10.9  |
      | C      | Sell | 70       | Limit      | 10.9  |
      | D      | Sell | 125      | Limit      | 11    |

  Scenario: Example 1 Step 1
    Then remaining buy order book depth is 4
    And remaining sell order book depth is 3

  Scenario: Example 1 Step 2
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | E      | Buy  | 200      | Limit      | 10.8  |
    Then remaining buy order book depth is 5
    And remaining sell order book depth is 3
    And best limits are:
      | Side | Limit |
      | Buy  | 10,8  |
      | Sell | 10,9  |

  Scenario: Example 1 Step 3
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | E      | Buy  | 200      | Limit      | 10.8  |
      | G      | Buy  | 100      | Limit      | 10.9  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | G             | C              | 100      | 10.9  |

  Scenario: Example 1 Step 4
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type | Price |
      | E      | Buy  | 200      | Limit      | 10.8  |
      | G      | Buy  | 100      | Limit      | 10.9  |
      | G      | Sell | 250      | Limit      | 10.8  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | G             | C              | 100      | 10.9  |
      | E             | G              | 200      | 10.8  |
      | B             | G              | 50       | 10.8  |
    And remaining buy order book depth is 4
    And remaining sell order book depth is 3
    And best limits are:
      | Side | Limit |
      | Buy  | 10,5  |
      | Sell | 10,9  |
