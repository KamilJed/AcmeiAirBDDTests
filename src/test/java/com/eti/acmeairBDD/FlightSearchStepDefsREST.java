package com.eti.acmeairBDD;

import com.mongodb.DBObject;
import io.cucumber.java.After;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class FlightSearchStepDefsREST {

        AcmeAirREST acmeAirClient = new AcmeAirREST();
        AcmeAirDatabase database;

        String user;
        String password;
        JSONObject flights;
        JSONObject userProfileInformation;

    {
        try {
            database = new AcmeAirDatabase();
        } catch (UnknownHostException e) {
            Assert.fail();
        }
    }

    @Given("^user (.+) is logged in with password (.+)$")
    public void userXIsLoggedInWithPasswordY(String user, String password) {
        Assert.assertTrue(acmeAirClient.logInUser(user, password));
        this.user = user;
        this.password = password;
    }

    @Given("^there is a flight from (.+) to (.+) on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) arriving ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void thereIsAFlightFromXToYOnZ(String fromAirport, String toAirport, int departDay, int departMonth, int departYear,
                                          int arriveDay, int arriveMonth, int arriveYear) {
        database.addFlightFromTo(fromAirport, toAirport, ZonedDateTime.of(departYear, departMonth, departDay, 23, 0, 0, 0, ZoneId.of("UTC")).minusDays(1), ZonedDateTime.of(arriveYear, arriveMonth, arriveDay, 23, 0, 0, 0, ZoneId.of("UTC")).minusDays(1));
    }

    @And("^user (.+) is registered in system with password (.+)$")
    public void userXIsRegisteredInSystemWithPasswordY(String user, String password) {
        database.insertDummyCustomer(user, password);
    }

    @Given("^user (.+) is registered in system with following data: password = (.+), phone number = (.+), status = (.+), street address = (.+), city = (.+), state province = (.+), country = (.+), postal code = (.+), phone number type = (.+)$")
    public void userXIsRegisteredInSystemWithFullData(String user, String password, String phoneNumber, String status, String streetAddress, String city, String stateProvince, String country, String postalCode, String phoneNumberType){
        database.insertCustomer(user, password, status, 0, 0, streetAddress, city, stateProvince, country, postalCode, phoneNumber, phoneNumberType);
    }

    @When("^user searches for flights from (.+) to (.+) departing on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) and back from .+ to .+ on ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userSearchesForFlightsFromXToYDepartingOnZ(String fromAirport, String toAirport, int departDay, int departMonth, int departYear, int returnDay, int returnMonth, int returnYear) {
        flights = acmeAirClient.searchFlights(database.getAirportNameMapping(fromAirport), database.getAirportNameMapping(toAirport), ZonedDateTime.of(departYear, departMonth, departDay, 0, 0, 0, 0, ZoneId.of("UTC")), ZonedDateTime.of(returnYear, returnMonth, returnDay, 0, 0, 0, 0, ZoneId.of("UTC")), "false");
        System.out.println(flights);
    }


    @Then("^user receives the following flight in search results: (.+) to (.+) on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) arriving ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userReceivesTheFollowingFlightInSearchResults(String fromAirport, String toAirport, int departDay, int departMonth, int departYear,
                                                              int arriveDay, int arriveMonth, int arriveYear) {
        boolean flightFound = false;
        JSONArray tripFlights = (JSONArray) flights.get("tripFlights");
        for (int i = 0; i < tripFlights.length(); i++){
            JSONArray flightsOptions = tripFlights.getJSONObject(i).getJSONArray("flightsOptions");
            for (int j = 0; j < flightsOptions.length(); j++){
                JSONObject flight = flightsOptions.getJSONObject(j);
                if (flight.getJSONObject("flightSegment").getString("originPort").equals(database.getAirportNameMapping(fromAirport))
                    && flight.getJSONObject("flightSegment").getString("destPort").equals(database.getAirportNameMapping(toAirport))){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date departureTimeAcme = dateFormat.parse(flight.getString("scheduledDepartureTime"));
                        Date arrivalTimeAcme = dateFormat.parse(flight.getString("scheduledArrivalTime"));
                        if (departureTimeAcme.equals(new Date(departYear - 1900, departMonth - 1, departDay))
                            && arrivalTimeAcme.equals(new Date(arriveYear - 1900, arriveMonth - 1, arriveDay))){
                            flightFound = true;
                            break;
                        }
                    } catch (ParseException ignored) {
                    }
                }
            }
            if (flightFound)
                break;
        }
        Assert.assertTrue(flightFound);
    }

    @When("^user (.+) asks for his profile information$")
    public void userXAsksForProfileInformation(String user){
        userProfileInformation = acmeAirClient.getUserByLogin(user);
    }

    @Then("^user receives the following information about his profile: id = (.+), password = (.+), phone number = (.+), status = (.+), street address = (.+), city = (.+), state province = (.+), country = (.+), postal code = (.+), phone number type = (.+)$")
    public void userReceivesInformationAboutProfile(String id, String password, String phoneNumber, String status, String streetAddress, String city, String stateProvince, String country, String postalCode, String phoneNumberType){
        Assert.assertEquals(id, userProfileInformation.getString("_id"));
        Assert.assertEquals(phoneNumberType, userProfileInformation.getString("phoneNumberType"));
        Assert.assertEquals(status, userProfileInformation.getString("status"));
        Assert.assertEquals(password, userProfileInformation.getString("password"));
        Assert.assertEquals(phoneNumber, userProfileInformation.getString("phoneNumber"));
        JSONObject addressInfo = userProfileInformation.getJSONObject("address");
        Assert.assertEquals(country, addressInfo.getString("country"));
        Assert.assertEquals(city, addressInfo.getString("city"));
        Assert.assertEquals(postalCode, addressInfo.getString("postalCode"));
        Assert.assertEquals(streetAddress, addressInfo.getString("streetAddress1"));
        Assert.assertEquals(stateProvince, addressInfo.getString("stateProvince"));
    }

    @When("^user books a flight from (.+) to (.+) departing on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) and a flight back from .+ to .+ on ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userBooksFlightFromXToY(String fromAirport, String toAirport, int departDay, int departMonth, int departYear, int returnDay, int returnMonth, int returnYear){
        DBObject departFlight = database.getFlightFromToOn(fromAirport, toAirport, new Date(departYear - 1900, departMonth - 1, departDay));
        Assert.assertNotNull(departFlight);
        DBObject returnFlight = database.getFlightFromToOn(toAirport, fromAirport, new Date(returnYear - 1900, returnMonth - 1, returnDay));
        Assert.assertNotNull(returnFlight);
        Assert.assertTrue(acmeAirClient.bookFlights(user, (String)departFlight.get("_id"), (String)returnFlight.get("_id"), "false"));
    }

    @Then("^user has the following flight in his bookings: (.+) to (.+) on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) arriving ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userHasFlightInBookings(String fromAirport, String toAirport, int departDay, int departMonth, int departYear, int arrivalDay, int arrivalMonth, int arrivalYear){
        Assert.assertTrue(isTheFlightBooked(fromAirport, toAirport, departDay, departMonth, departYear, arrivalDay, arrivalMonth, arrivalYear));
    }

    @Then("^user does not have the following flight in his bookings: (.+) to (.+) on ([0-9]{2})-([0-9]{2})-(2[0-9]{3}) arriving ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userDoesNotHaveFlightInBookings(String fromAirport, String toAirport, int departDay, int departMonth, int departYear, int arrivalDay, int arrivalMonth, int arrivalYear) {
        Assert.assertFalse(isTheFlightBooked(fromAirport, toAirport, departDay, departMonth, departYear, arrivalDay, arrivalMonth, arrivalYear));
    }

    @When("^user cancels booking of a flight from (.+) to (.+) on ([0-9]{2})-([0-9]{2})-(2[0-9]{3})$")
    public void userCancelsBookingOfAFlightFromXToYOnZ(String fromAirport, String toAirport, int departDay, int departMonth, int departYear) {
        DBObject flightToCancel = database.getFlightFromToOn(fromAirport, toAirport, new Date(departYear - 1900, departMonth - 1, departDay));
        DBObject bookingToCancel = database.getBookingByFlightId(user, (String) flightToCancel.get("_id"));
        acmeAirClient.cancelBooking(user, (String)bookingToCancel.get("_id"));
    }

    @When("^user changes their info to: phone number = (.+), street address = (.+), city = (.+), state province = (.+), country = (.+), postal code = (.+), phone number type = (.+), status = (.+)$")
    public void userChangesInfo(String phoneNumber, String streetAddress, String city, String stateProvince, String country, String postalCode, String phoneNumberType, String status){
        Assert.assertTrue(acmeAirClient.changeUserInformation(user, password, phoneNumber, phoneNumberType, city, country, postalCode, stateProvince, streetAddress, status));
    }

    @After
    public void clearData(){
        database.clearAllCollections();
    }

    private boolean isTheFlightBooked(String fromAirport, String toAirport, int departDay, int departMonth, int departYear, int arrivalDay, int arrivalMonth, int arrivalYear){
        boolean isFlightBooked = false;
        JSONArray bookings = acmeAirClient.getBookingsByUser(user);
        Assert.assertNotNull(bookings);
        for (int i = 0; i < bookings.length(); i++){
            JSONObject flightBooked = bookings.getJSONObject(i);
            DBObject flightBookedInfo = database.getFlightById(flightBooked.getString("flightId"));
            Assert.assertNotNull(flightBookedInfo);
            DBObject flightSegment = database.getFlightSegmentById((String)flightBookedInfo.get("flightSegmentId"));
            Assert.assertNotNull(flightSegment);
            if (database.getAirportNameMapping(fromAirport).equals(flightSegment.get("originPort")) && database.getAirportNameMapping(toAirport).equals(flightSegment.get("destPort"))
                    && flightBookedInfo.get("scheduledDepartureTime").equals(new Date(departYear - 1900, departMonth - 1, departDay)) && flightBookedInfo.get("scheduledArrivalTime").equals(new Date(arrivalYear - 1900, arrivalMonth - 1, arrivalDay))){
                isFlightBooked = true;
                break;
            }
        }
        return isFlightBooked;
    }
}
