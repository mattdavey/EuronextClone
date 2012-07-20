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
    Then "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | D      | Buy  | 100      | Limit      | 10.5  |
      | B      | Buy  | 100      | Limit      | 10.3  |
      | A      | Buy  | 100      | Limit      | 10.2  |
      | C      | Buy  | 100      | Limit      | 10.1  |

  Scenario: Limit sell orders are sorted from low to high
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Sell | 100      | 10.2  |
      | B      | Sell | 100      | 10.3  |
      | C      | Sell | 100      | 10.1  |
      | D      | Sell | 100      | 10.5  |
    Then "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 100      | Limit      | 10.1  |
      | A      | Sell | 100      | Limit      | 10.2  |
      | B      | Sell | 100      | Limit      | 10.3  |
      | D      | Sell | 100      | Limit      | 10.5  |

  Scenario: Equal limit orders are sorted by their arrival
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | 10.2  |
      | B      | Buy  | 100      | 10.2  |
      | C      | Sell | 100      | 10.3  |
      | D      | Sell | 100      | 10.3  |
    Then "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | A      | Buy  | 100      | Limit      | 10.2  |
      | B      | Buy  | 100      | Limit      | 10.2  |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type | Price |
      | C      | Sell | 100      | Limit      | 10.3  |
      | D      | Sell | 100      | Limit      | 10.3  |

  Scenario: Market orders are sorted by their arrival
    Given the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 100      | MO    |
      | B      | Buy  | 100      | MTL   |
      | C      | Buy  | 100      | MO    |
      | D      | Sell | 100      | MO    |
      | E      | Sell | 100      | MTL   |
      | F      | Sell | 100      | MO    |
    Then "Buy" order book should look like:
      | Broker | Side | Quantity | Order Type    | Price |
      | A      | Buy  | 100      | MarketOrder   |       |
      | B      | Buy  | 100      | MarketToLimit |       |
      | C      | Buy  | 100      | MarketOrder   |       |
    And "Sell" order book should look like:
      | Broker | Side | Quantity | Order Type    | Price |
      | D      | Sell | 100      | MarketOrder   |       |
      | E      | Sell | 100      | MarketToLimit |       |
      | F      | Sell | 100      | MarketOrder   |       |
