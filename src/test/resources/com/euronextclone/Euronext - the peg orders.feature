Feature: Examples from the Euronext the Peg Orders PDF

  Background:
    Given that trading mode for security is "Continuous" and phase is "CoreContinuous"

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
    And the book looks like:
      | Broker | Quantity | Price     | Price | Quantity | Broker |
      | A      | 200      | 10.5      | 10.9  | 130      | C      |
      | B      | 150      | Peg(10.5) | 10.9  | 350      | C      |
      | B      | 70       | Peg(10.5) | 11    | 275      | D      |
      | B      | 125      | 10.5      |       |          |        |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | E      | Buy  | 200      | 10.8  |
    Then no trades are generated
    And the book looks like:
      | Broker | Quantity | Price     | Price | Quantity | Broker |
      | E      | 200      | 10.8      | 10.9  | 130      | C      |
      | B      | 150      | Peg(10.8) | 10.9  | 350      | C      |
      | B      | 70       | Peg(10.8) | 11    | 275      | D      |
      | A      | 200      | 10.5      |       |          |        |
      | B      | 125      | 10.5      |       |          |        |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Buy  | 100      | 10.9  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | G             | C              | 100      | 10.9  |
    And the book looks like:
      | Broker | Quantity | Price     | Price | Quantity | Broker |
      | E      | 200      | 10.8      | 10.9  | 30       | C      |
      | B      | 150      | Peg(10.8) | 10.9  | 350      | C      |
      | B      | 70       | Peg(10.8) | 11    | 275      | D      |
      | A      | 200      | 10.5      |       |          |        |
      | B      | 125      | 10.5      |       |          |        |
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | G      | Sell | 250      | 10.8  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | E             | G              | 200      | 10.8  |
      | B             | G              | 50       | 10.8  |
    And the book looks like:
      | Broker | Quantity | Price     | Price | Quantity | Broker |
      | A      | 200      | 10.5      | 10.9  | 30       | C      |
      | B      | 125      | 10.5      | 10.9  | 350      | C      |
      | B      | 100      | Peg(10.5) | 11    | 275      | D      |
      | B      | 70       | Peg(10.5) |       |          |        |

  @focus
  Scenario: Example 2
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price     |
      | A      | Buy  | 200      | 11.5      |
      | B      | Buy  | 150      | Peg[11.6] |
      | B      | Buy  | 70       | Peg       |
      | B      | Buy  | 125      | 10.5      |
      | C      | Sell | 130      | 11.8      |
      | C      | Sell | 350      | 11.9      |
      | D      | Sell | 275      | 12        |
    And the book looks like:
      | Broker | Quantity | Price           | Price | Quantity | Broker |
      | A      | 200      | 11.5            | 11.8  | 130      | C      |
      | B      | 150      | Peg(11.5)[11.6] | 11.9  | 350      | C      |
      | B      | 70       | Peg(11.5)       | 12    | 275      | D      |
      | B      | 125      | 10.5            |       |          |        |
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | E      | Buy  | 200      | 11.7  |
    And the book looks like:
      | Broker | Quantity | Price           | Price | Quantity | Broker |
      | E      | 200      | 11.7            | 11.8  | 130      | C      |
      | B      | 70       | Peg(11.7)       | 11.9  | 350      | C      |
      | B      | 150      | Peg(11.6)[11.6] | 12    | 275      | D      |
      | A      | 200      | 11.5            |       |          |        |
      | B      | 125      | 10.5            |       |          |        |
    When the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 270      | 11.7  |
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | E             | A              | 200      | 11.7  |
      | B             | A              | 70       | 11.7  |
    And the book looks like:
      | Broker | Quantity | Price           | Price | Quantity | Broker |
      | A      | 200      | 11.5            | 11.8  | 130      | C      |
      | B      | 150      | Peg(11.5)[11.6] | 11.9  | 350      | C      |
      | B      | 125      | 10.5            | 12    | 275      | D      |
