Feature: Flight booking

  Background:
    Given there is a flight from Mumbai to Hong Kong on 14-02-2022 arriving 14-02-2022
    And there is a flight from Hong Kong to Mumbai on 15-02-2022 arriving 15-02-2022
    And user john@email.com is registered in system with password pass
    And user uid1@email.com is registered in system with password hispass


  Scenario: Book flights
    Given user john@email.com is logged in with password pass
    When user books a flight from Mumbai to Hong Kong departing on 14-02-2022 and a flight back from Hong Kong to Mumbai on 15-02-2022
    Then user has the following flight in his bookings: Mumbai to Hong Kong on 14-02-2022 arriving 14-02-2022
    And user has the following flight in his bookings: Hong Kong to Mumbai on 15-02-2022 arriving 15-02-2022


  Scenario: Cancel booked flight:
    Given user john@email.com is logged in with password pass
    And user books a flight from Mumbai to Hong Kong departing on 14-02-2022 and a flight back from Hong Kong to Mumbai on 15-02-2022
    When user cancels booking of a flight from Mumbai to Hong Kong on 14-02-2022
    Then user has the following flight in his bookings: Hong Kong to Mumbai on 15-02-2022 arriving 15-02-2022
    And user does not have the following flight in his bookings: Mumbai to Hong Kong on 14-02-2022 arriving 14-02-2022