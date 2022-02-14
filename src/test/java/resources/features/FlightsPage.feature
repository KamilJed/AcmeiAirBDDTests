Feature: Flight search

  Background:
    Given there is a flight from Paris to London on Monday February 14 2022
    And there is a flight from London to Paris on Monday February 14 2022
    And user john@email.com is registered in system with password pass
    And user uid1@email.com is registered in system with password hispass


  Scenario: Search flights
    Given user john@email.com is logged in with password pass
    And user searches for flights from London to Paris departing on Monday February 14 2022 and back on Monday February 14 2022
    Then user receives the following flight in search results: LON PAR Monday 10.02.2022 11:00 UTC
    And user receives the following flight in search results: LON PAR Monday 17.02.2022 11:00 UTC



