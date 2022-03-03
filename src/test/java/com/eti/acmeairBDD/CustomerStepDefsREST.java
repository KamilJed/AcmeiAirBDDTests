package com.eti.acmeairBDD;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.net.UnknownHostException;

public class CustomerStepDefsREST {

    AcmeAirREST acmeAirClient = new AcmeAirREST();
    AcmeAirDatabase database;

    {
        try {
            database = new AcmeAirDatabase();
        } catch (UnknownHostException e) {
            Assert.fail();
        }
    }

//    @Given("^user (.+) is logged in with password (.+)$")
//    public void userXIsLoggedInWithPasswordY(String user, String password) {
//        Assert.assertTrue(acmeAirClient.logInUser(user, password));
//    }

}
