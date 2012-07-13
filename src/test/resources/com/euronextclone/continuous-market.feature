Feature: Matching Rules in continuous matching mode

  Scenario: Continuous mode call phase
    Given a MTL buy order from broker "A" for 50 shares
    And a MTL sell order from broker "B" for 40 shares
    Then remaining buy order book depth is 1
    And remaining sell order book depth is 0

  Scenario: Continuous mode call phase with  Market to limits, Pure Market orders and limited orders
    Given a MTL buy order from broker "A" for 10 shares
    And a MO buy order from broker "B" for 10 shares
    And a MTL buy order from broker "C" for 10 shares
    And a Limit sell order from broker "D" for 10 shares at 10
    Then remaining buy order book depth is 2
    And remaining sell order book depth is 0