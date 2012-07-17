Feature: Calculating Indicative Market Price Used in Auction Phase

  Background: Given that market is in pre-opening phase

  Scenario: The Indicative Matching Price is higher than the best limit & equal to the reference price
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | MarketOrder |       |
      | G      | Sell | 40       | Limit       | 9.98  |
    Then the calculated IMP is:
      | 10 |

  Scenario: - The Indicative Matching Price is equal to the best limit & lower to the reference price Reference price
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | MarketOrder |       |
      | G      | Sell | 41       | Limit       | 9.98  |
    Then the calculated IMP is:
      | 9.98 |

  Scenario: - The Indicative Matching Price is equal to the best limit & higher to the reference price Reference price
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | MarketOrder |       |
      | G      | Sell | 40       | Limit       | 10.02 |
    Then the calculated IMP is:
      | 10.02 |

  Scenario: - The Indicative Matching Price should be the one clearing most shares in the presence of multiple sell limit prices
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 80       | MarketOrder |       |
      | G      | Sell | 40       | Limit       | 10.02 |
      | H      | Sell | 40       | Limit       | 10.04 |
    Then the calculated IMP is:
      | 10.04 |

  @focus
  Scenario: - The Indicative Matching Price should be the one clearing most shares in the presence of multiple buy limit prices
    Given that reference price is 10
    And the following orders are submitted in this order:
      | Broker | Side | Quantity | Order Type  | Price |
      | A      | Buy  | 40       | Limit       | 9.98  |
      | B      | Buy  | 40       | Limit       | 9.96  |
      | G      | Sell | 80       | MarketOrder |       |
    Then the calculated IMP is:
      | 9.96 |
