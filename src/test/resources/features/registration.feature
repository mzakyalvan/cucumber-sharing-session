Feature: Partner Registrations
  As a candidate partner, I want to be able to register my organization
  So that my team able to use b2b integration support provided by tiket.com

  Scenario Outline: Register with partner data
    Given Partner want to register from organization "<organizationName>" contact person "<contactPerson>" business phone "<phoneNumber>" and business email "<emailAddress>"
    And Already registered business partners
#     | organizationName    | contactPerson | phoneNumber | emailAddress            | partnershipState  |
      | Registered Partner  | Bang Subur    | 021765345   | registered@example.com  | ACTIVE            |
    And Registration endpoint mapped to "/bitubi/registry"
    When Submit registration data to registration endpoint
    Then Partner registered when required data are valid "<dataValid>"
    And Confirmation with credential email sent to given business email address "<emailAddress>"
    Examples:
      | organizationName    | contactPerson | phoneNumber | emailAddress            | dataValid       |
      | Unregistered Partner| Agus Gamang   | 021323432   | blabla@sample.com       | true            |
      | Another Unregistered| Ucup Sengklek | 234234324   | other@example.com       | true            |
      | Registered Partner  | Bang Subur    | 021765345   | registered@example.com  | false           |
      | Invalid Registrar   |               | 871231234   |                         | false           |