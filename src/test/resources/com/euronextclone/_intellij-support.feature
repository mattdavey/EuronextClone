Feature: Cucumber-JVM support in IntelliJ
  @focus
  Scenario: the market order is totally filled
    Given that reference orderTypeLimit is 10.0
    And the following orders submitted to the book:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | A          | 1           | 50           | MTL       | MTL       | 40           | 4           | D          |
      | G          | 7           | 20           | MO        | 10.08     | 100          | 5           | E          |
      | B          | 2           | 90           | 10.1      | 10.15     | 60           | 6           | F          |
      | C          | 3           | 10           | 9.9       |           |              |             |            |
    When class auction completes
    Then the following trades are generated:
      | Buying broker | Selling broker | Quantity | Price |
      | A             | D              | 40       | 10.1  |
      | A             | E              | 10       | 10.1  |
      | G             | E              | 20       | 10.1  |
      | B             | E              | 70       | 10.1  |
    And the book looks like:
      | Bid Broker | Bid OrderId | Bid Quantity | Bid Price | Ask Price | Ask Quantity | Ask OrderId | Ask Broker |
      | B          | 2           | 20           | 10.1      | 10,15     | 60           | 6           | F          |
      | C          | 3           | 10           | 9.9       |           |              |             |            |
