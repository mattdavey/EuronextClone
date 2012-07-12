Feature: Matching Rules in continuous matching mode

  Scenario: Continuous mode call phase
    Given the MTL buy order from broker "A" for 50 shares
    And the MTL sell order from broker "B" for 40 shares
    Then remaining buy order book depth is 1
    And remaining sell order book depth is 0

  Scenario: Continuous mode call phase with  Market to limits, Pure Market orders and limited orders
    Given the MTL buy order from broker "A" for 10 shares
    And the MO buy order from broker "B" for 10 shares
    And the MTL buy order from broker "C" for 10 shares
    And the Limit sell order from broker "D" for 10 shares
    Then remaining buy order book depth is 2
    And remaining sell order book depth is 0