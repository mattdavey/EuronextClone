Feature: Calculating Indicative Market Price Used in Auction Phase

  Background: Given that market is in pre-opening phase

  @focus
  Scenario: The Indicative Matching Price is higher than the best limit & equal to the reference price
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | MarketOrder |       |
      | G      | Sell | 40       | Limit       | 9.98  |
    Then the calculated IMP is:
      | 10 |

  @focus
  Scenario: - The Indicative Matching Price is equal to the best limit & lower to the reference price Reference price
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | MarketOrder |       |
      | G      | Sell | 41       | Limit       | 9.98  |
    Then the calculated IMP is:
      | 9.98 |
