package com.eti.acmeairBDD;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Date;

public class AcmeAirDatabase {
    static final String ACMEAIR_DATABASE_NAME = "acmeair";

    static final String ACMEAIR_CUSTOMER_COLLECTION = "customer";
    static final String ACMEAIR_AIRPORT_CODE_MAPPINGS_COLLECTION = "airportCodeMapping";
    static final String ACMEAIR_FLIGHT_COLLECTION = "flight";
    static final String ACMEAIR_FLIGHT_SEGMENT_COLLECTION = "flightSegment";
    static final String ACMEAIR_BOOKING_COLECTION = "booking";

    static final String ACMEAIR_CUSTOMER_CLASS = "com.acmeair.morphia.entities.CustomerImpl";
    static final String ACMEAIR_ADDRESS_CLASS = "com.acmeair.morphia.entities.CustomerAddressImpl";
    static final String ACMEAIR_FLIGHT_CLASS = "com.acmeair.morphia.entities.FlightImpl";
    static final String ACMEAIR_FLIGHT_SEGMENT_CLASS = "com.acmeair.morphia.entities.FlightSegmentImpl";

    static final String ACMEAIR_FLIGHT_SEGMENT_ID_PREFIX = "AA";


    MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

    public AcmeAirDatabase() throws UnknownHostException {
    }

    public void insertCustomer(String name, String password, String status, int totalMiles, int milesYTD,
                                  String streetAddress, String city, String stateProvince, String country,
                                  String postalCode, String phoneNumber, String phoneNumberType){
        DBCollection collection = getCollection(ACMEAIR_CUSTOMER_COLLECTION);
        DBObject query = new BasicDBObject("_id", name);
        DBObject obj = collection.findOne(query);
        if (obj == null){
            DBObject newUser = new BasicDBObject("_id", name)
//                    .append("className", ACMEAIR_CUSTOMER_CLASS)
                    .append("password", password)
                    .append("status", status)
                    .append("total_miles", totalMiles)
                    .append("miles_ytd", milesYTD)
                    .append("address", new BasicDBObject("streetAddress1", streetAddress)
//                            .append("streetAddress1", streetAddress)
                            .append("city", city)
                            .append("stateProvince", stateProvince)
                            .append("country", country)
                            .append("postalCode", postalCode))
                    .append("phoneNumber", phoneNumber)
                    .append("phoneNumberType", phoneNumberType);
            collection.insert(newUser);
        }
    }

    public void insertDummyCustomer(String name, String password){
        insertCustomer(name, password, "GOLD", 100000, 1000, "123 Main St.", "Anytown",
                "NC", "USA", "27617", "919-123-4567", "BUSINESS");
    }

    public String getAirportNameMapping(String airport){
        String mapping = "";
        DBCollection collection = getCollection(ACMEAIR_AIRPORT_CODE_MAPPINGS_COLLECTION);
        DBObject query = new BasicDBObject("airportName", airport);
        DBObject mapObject = collection.findOne(query);
        if (mapObject != null){
            mapping = (String)mapObject.get("_id");
        }
        return mapping;
    }

    public void addFlightSegmentFromTo(String from, String to){
        DBObject flightSegment = getFlightSegmentFromTo(from, to);
        if (flightSegment == null){
            DBCollection collection = getCollection(ACMEAIR_FLIGHT_SEGMENT_COLLECTION);
            long id = collection.count() + 1;
            DBObject newFlightSegment = new BasicDBObject("_id", ACMEAIR_FLIGHT_SEGMENT_ID_PREFIX.concat(Long.toString(id)))
//                    .append("className", ACMEAIR_FLIGHT_SEGMENT_CLASS)
                    .append("originPort", getAirportNameMapping(from))
                    .append("destPort", getAirportNameMapping(to))
                    .append("miles", 666);
            collection.insert(newFlightSegment);
        }
    }

    public DBObject getFlightSegmentFromTo(String from, String to){
        DBCollection collection = getCollection(ACMEAIR_FLIGHT_SEGMENT_COLLECTION);
        DBObject query = new BasicDBObject("originPort", getAirportNameMapping(from))
                .append("destPort", getAirportNameMapping(to));
        return collection.findOne(query);
    }

    public void addFlightFromTo(String from, String to, ZonedDateTime departureTime, ZonedDateTime arrivalTime){
        DBCollection collection = getCollection(ACMEAIR_FLIGHT_COLLECTION);
        DBObject segment = getFlightSegmentFromTo(from, to);
        if (segment == null){
            addFlightSegmentFromTo(from, to);
            segment = getFlightSegmentFromTo(from, to);
        }
        DBObject newFlight = new BasicDBObject("_id",  java.util.UUID.randomUUID().toString())
//                .append("className", ACMEAIR_FLIGHT_CLASS)
                .append("flightSegmentId", (String)segment.get("_id"))
                .append("scheduledDepartureTime", Date.from(departureTime.toInstant()))
                .append("scheduledArrivalTime", Date.from(arrivalTime.toInstant()))
                .append("firstClassBaseCost", "600")
                .append("economyClassBaseCost", "300")
                .append("numFirstClassSeats", 10)
                .append("numEconomyClassSeats", 200)
                .append("airplaneTypeId", "B747");
        collection.insert(newFlight);
    }

    public void clearAllCollections(){
        clearCollection(ACMEAIR_CUSTOMER_COLLECTION);
        clearCollection(ACMEAIR_FLIGHT_COLLECTION);
        clearCollection(ACMEAIR_FLIGHT_SEGMENT_COLLECTION);
        clearCollection(ACMEAIR_BOOKING_COLECTION);
    }

    public void clearCollection(String collection){
        DBCollection collectionToClear = getCollection(collection);
        collectionToClear.remove(new BasicDBObject());
    }

    public DBObject getFlightFromToOn(String fromAirport, String toAirport, Date date){
        DBObject flightFromTo = null;
        DBObject segment = getFlightSegmentFromTo(fromAirport, toAirport);
        DBObject query = new BasicDBObject("flightSegmentId", (String)segment.get("_id"));
        try(Cursor cursor = getCollection(ACMEAIR_FLIGHT_COLLECTION).find(query)){
            while(cursor.hasNext()){
                DBObject flight = cursor.next();
                Date departureTime = (Date)flight.get("scheduledDepartureTime");
                if (departureTime.equals(date)){
                    flightFromTo = flight;
                    break;
                }
            }
        }
        return flightFromTo;
    }

    public DBObject getFlightById(String id){
        DBObject query = new BasicDBObject("_id", id);
        return getCollection(ACMEAIR_FLIGHT_COLLECTION).findOne(query);
    }

    public DBObject getFlightSegmentById(String id){
        DBObject query = new BasicDBObject("_id", id);
        return getCollection(ACMEAIR_FLIGHT_SEGMENT_COLLECTION).findOne(query);
    }

    public DBObject getBookingByFlightId(String user, String id){
        DBObject query = new BasicDBObject("customerId", user)
                .append("flightId", id);
        return getCollection(ACMEAIR_BOOKING_COLECTION).findOne(query);
    }

    private DBCollection getCollection(String collection){
        DB database = mongoClient.getDB(ACMEAIR_DATABASE_NAME);
        return database.getCollection(collection);
    }
}
