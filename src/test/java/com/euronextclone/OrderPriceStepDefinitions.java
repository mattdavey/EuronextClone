package com.euronextclone;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/15/12
 * Time: 6:40 PM
 */
public class OrderPriceStepDefinitions {

    private OrderTypeLimit first;
    private OrderTypeLimit second;

    @Given("^the first order type is \"([^\"]*)\"$")
    public void the_first_order_type_is(OrderType type) throws Throwable {
        first = new OrderTypeLimit(type);
    }

    @Given("^the second order type is \"([^\"]*)\"$")
    public void the_second_order_type_is(OrderType type) throws Throwable {
        second = new OrderTypeLimit(type);
    }

    @Then("^the first order type should compare equal to the second$")
    public void the_first_order_type_should_compare_equal_to_the_second() throws Throwable {

//        assertThat(first.compareTo(second), is(0));
    }
}
