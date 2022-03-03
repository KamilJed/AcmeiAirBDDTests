Feature: Flight search

  Background:
    Given there is a flight from Mumbai to Hong Kong on 14-02-2022 arriving 14-02-2022
    And there is a flight from Hong Kong to Mumbai on 15-02-2022 arriving 15-02-2022
    And user john@email.com is registered in system with password pass
    And user uid1@email.com is registered in system with password hispass


  Scenario: Search flights
    Given user john@email.com is logged in with password pass
    When user searches for flights from Mumbai to Hong Kong departing on 14-02-2022 and back from Hong Kong to Mumbai on 15-02-2022
    Then user receives the following flight in search results: Mumbai to Hong Kong on 14-02-2022 arriving 14-02-2022
    And user receives the following flight in search results: Hong Kong to Mumbai on 15-02-2022 arriving 15-02-2022
