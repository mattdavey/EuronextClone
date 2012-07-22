Feature: Pure market orders

  Scenario: Resting market order is partially filled by incoming limit order
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | MO    |
      | B      | Sell | 60       | 10.3  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | B              | 60       | 10.3  |
    And the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 40       | MO    |       |          |        |
