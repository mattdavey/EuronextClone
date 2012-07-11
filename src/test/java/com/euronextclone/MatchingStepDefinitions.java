package com.euronextclone;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;

public class MatchingStepDefinitions {

  @Given("^that reference price is (\\d+)$")
  public void that_reference_price_is(int arg1) throws Throwable {
    // Express the Regexp above with the code you wish you had
    // throw new PendingException();
  }

  @Given("^the following orders submitted to the book:$")
  public void the_following_orders_submitted_to_the_book(DataTable arg1) throws Throwable {
    // Express the Regexp above with the code you wish you had
    // For automatic conversion, change DataTable to List<YourType>
    // throw new PendingException();
  }

  @When("^class auction completes$")
  public void class_auction_completes() throws Throwable {
    // Express the Regexp above with the code you wish you had
    // throw new PendingException();
  }

  @Then("^the following trades are generated:$")
  public void the_following_trades_are_generated(DataTable arg1) throws Throwable {
    // Express the Regexp above with the code you wish you had
    // For automatic conversion, change DataTable to List<YourType>
    // throw new PendingException();
  }

  @Then("^the book looks like:$")
  public void the_book_looks_like(DataTable arg1) throws Throwable {
    // Express the Regexp above with the code you wish you had
    // For automatic conversion, change DataTable to List<YourType>
    // throw new PendingException();
  }
}
