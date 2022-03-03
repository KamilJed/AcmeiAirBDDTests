Feature: Customer Service

  Background:
    Given user not_john@email.com is registered in system with following data: password = not_pass, phone number = 919-123-4567, status = GOLD, street address = Sesame Street 12, city = London, state province = London, country = United Kingdom, postal code = SW1W 0NY, phone number type = BUSINESS

  Scenario: Get profile information
    Given user not_john@email.com is logged in with password not_pass
    When user not_john@email.com asks for his profile information
    Then user receives the following information about his profile: id = not_john@email.com, password = not_pass, phone number = 919-123-4567, status = GOLD, street address = Sesame Street 12, city = London, state province = London, country = United Kingdom, postal code = SW1W 0NY, phone number type = BUSINESS

  Scenario: Update profile info
    Given user not_john@email.com is logged in with password not_pass
    When user changes their info to: phone number = 123-123-123, street address = Not Street 1, city = Not City, state province = Paper, country = Cardboard, postal code = QQQ QQQ, phone number type = MOBILE, status = GOLD
    And user not_john@email.com asks for his profile information
    Then user receives the following information about his profile: id = not_john@email.com, password = not_pass, phone number = 123-123-123, status = GOLD, street address = Not Street 1, city = Not City, state province = Paper, country = Cardboard, postal code = QQQ QQQ, phone number type = MOBILE