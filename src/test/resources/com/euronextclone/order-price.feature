Feature: Order Price

  @focus
  Scenario: Two market orders should be considered equal
    Given the first order type is "MarketOrder"
    And the second order type is "MarketOrder"
    Then the first order type should compare equal to the second
