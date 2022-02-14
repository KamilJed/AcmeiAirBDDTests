package com.eti.acmeairBDD;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.json.JSONObject;
import org.junit.Assert;

import java.net.UnknownHostException;
import java.util.Date;

import static org.junit.Assert.*;

public class FlightSearchStepDefsREST {

        AcmeAirREST acmeAirClient = new AcmeAirREST();
        AcmeAirDatabase database;

    {
        try {
            database = new AcmeAirDatabase();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Given("^user (.+) is logged in with password (.+)$")
        public void thereIsAFlightFromTo(String user, String password) {
            Assert.assertTrue(acmeAirClient.logInUser(user, password));
//            JSONObject flights = acmeAirClient.searchFlights("CDG", "LHR", "Mon Feb 14 2022", "Mon Feb 14 2022", "false");
//
//            System.out.println(flights);
        }

    @Given("^there is a flight from (.+) to (.+) on ([A-Z][a-z]+) ([A-Z][a-z]+) ([0-9]{2}) (2[0-9]{3})$")
    public void thereIsAFlightFromXToYOnZ(String fromAirport, String toAirport, String dayOfWeek, String month, int day, int year) {
        database.addFlightFromTo(fromAirport, toAirport, new Date(), new Date());
    }

    @And("^user (.+) is registered in system with password (.+)$")
    public void userJohnEmailComIsRegisteredInSystemWithPasswordMypass(String user, String password) {
        database.insertDummyCustomer("uid150@email.com", "pass");
    }
}
