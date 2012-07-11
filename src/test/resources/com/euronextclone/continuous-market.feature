Feature: Matching Rules in continuous matching mode
  Scenario: Continuous mode call phase
    Given the MTL buy order from broker "A" for 50 shares
    And the MTL sell order from broker "B" for 40 shares
    Then remaining buy order book depth is 1
    And remaining sell order book depth is 0
