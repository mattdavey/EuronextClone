Feature: Basic order book construction

  Background:
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"

  Scenario: Limit buy orders are sorted from high to low
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | 10.2  |
      | B      | Buy  | 100      | 10.3  |
      | C      | Buy  | 100      | 10.1  |
      | D      | Buy  | 100      | 10.5  |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | D      | 100      | 10.5  |       |          |        |
      | B      | 100      | 10.3  |       |          |        |
      | A      | 100      | 10.2  |       |          |        |
      | C      | 100      | 10.1  |       |          |        |

  Scenario: Limit sell orders are sorted from low to high
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 100      | 10.3  |
      | C      | Sell | 100      | 10.1  |
      | D      | Sell | 100      | 10.5  |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      |        |          |       | 10.1  | 100      | C      |
      |        |          |       | 10.2  | 100      | A      |
      |        |          |       | 10.3  | 100      | B      |
      |        |          |       | 10.5  | 100      | D      |


  Scenario: Equal limit orders are sorted by their arrival
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | 10.2  |
      | B      | Buy  | 100      | 10.2  |
      | C      | Sell | 100      | 10.3  |
      | D      | Sell | 100      | 10.3  |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 100      | 10.2  | 10.3  | 100      | C      |
      | B      | 100      | 10.2  | 10.3  | 100      | D      |


  Scenario: Market orders are sorted by their arrival
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | MO    |
      | B      | Buy  | 100      | MTL   |
      | C      | Buy  | 100      | MO    |
      | D      | Sell | 100      | MO    |
      | E      | Sell | 100      | MTL   |
      | F      | Sell | 100      | MO    |
    Then the book looks like:
      | Broker | Quantity | Price | Price | Quantity | Broker |
      | A      | 100      | MO    | MO    | 100      | D      |
      | B      | 100      | MTL   | MTL   | 100      | E      |
      | C      | 100      | MO    | MO    | 100      | F      |
