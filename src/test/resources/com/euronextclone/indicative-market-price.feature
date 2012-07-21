Feature: Calculating Indicative Market Price Used in Auction Phase

  Background:
    Given that trading mode for security is "Continuous" and phase is "CoreAuction"

  Scenario: - The Indicative Matching Price should be the one clearing most shares in the presence of multiple sell limit prices
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 80       | MO    |
      | G      | Sell | 40       | 10.02 |
      | H      | Sell | 40       | 10.04 |
    Then the calculated IMP is:
      | 10.04 |

  Scenario: - The Indicative Matching Price should be the one clearing most shares in the presence of multiple buy limit prices
    Given that trading mode for security is "ByAuction" and phase is "CoreCall"
    And that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Price |
      | A      | Buy  | 40       | 9.98  |
      | B      | Buy  | 40       | 9.96  |
      | G      | Sell | 80       | MO    |
    Then the calculated IMP is:
      | 9.96 |
