package com.tiket.poc.bdd.feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.tiket.poc.SampleApplication;
import com.tiket.poc.dto.RegistrationForm;
import com.tiket.poc.entity.BusinessPartner;
import com.tiket.poc.entity.PartnershipState;
import com.tiket.poc.repo.BusinessPartnerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.Message;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(classes = SampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RegisterStepDefinition {
    @Autowired
    private BusinessPartnerRepository partnerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MailProperties mailProperties;

    @LocalServerPort
    private int boundPort;

    private String organizationName;
    private String contactPerson;
    private String phoneNumber;
    private String emailAddress;

    private String endpointPath;

    private Response serverResponse;

    private boolean registerSuccess;

    private GreenMail greenMail;

    @Before
    public void setUp() {
        RestAssured.baseURI = UriComponentsBuilder.newInstance()
                .scheme("http").host("localhost").port(boundPort)
                .build().toUriString();

        partnerRepository.deleteAll();

        ServerSetup serverSetup = new ServerSetup(mailProperties.getPort(), mailProperties.getHost(), ServerSetup.PROTOCOL_SMTP);
        serverSetup.setServerStartupTimeout(2_000);
        greenMail = new GreenMail(serverSetup);
        greenMail.setUser(mailProperties.getUsername(), mailProperties.getPassword());

        greenMail.start();
    }

    @After
    public void tearDown() {
        greenMail.stop();
    }

    @Given("^Partner want to register from organization \"([^\"]*)\" contact person \"([^\"]*)\" business phone \"([^\"]*)\" and business email \"([^\"]*)\"$")
    public void partnerWantToRegisterFromOrganizationContactPersonBusinessPhoneAndBusinessEmail(String organizationName, String contactPerson, String phoneNumber, String emailAddress) throws Throwable {
        this.organizationName = organizationName;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    @And("^Already registered business partners$")
    public void alreadyRegisteredBusinessPartners(DataTable table) throws Throwable {
        BusinessPartner registeredPartner = BusinessPartner.builder()
                .id(UUID.randomUUID())
                .organizationName(table.cell(0, 0))
                .contactName(table.cell(0, 1))
                .phoneNumber(table.cell(0, 2))
                .emailAddress(table.cell(0, 3))
                .partnershipState(PartnershipState.valueOf(table.cell(0, 4)))
                .registeredTime(LocalDateTime.now())
                .build();
        partnerRepository.save(registeredPartner);
    }

    @And("^Registration endpoint mapped to \"([^\"]*)\"$")
    public void registrationEndpointMappedTo(String endpointPath) throws Throwable {
        this.endpointPath = endpointPath;
    }

    @When("^Submit registration data to registration endpoint$")
    public void submitRegistrationDataToRegistrationEndpoint() throws Throwable {
        RegistrationForm form = RegistrationForm.builder()
                .organizationName(organizationName)
                .contactPerson(contactPerson)
                .businessEmail(emailAddress)
                .businessPhone(phoneNumber)
                .build();

        String requestBody = objectMapper.writeValueAsString(form);
        this.serverResponse = RestAssured.given().log().all(true).body(requestBody).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .when().log().all(true).post(endpointPath);
    }

    @Then("^Partner registered when required data are valid \"([^\"]*)\"$")
    public void partnerRegisteredWhenRequiredDataAreValid(String dataValid) throws Throwable {
        if(Boolean.parseBoolean(dataValid)) {
            registerSuccess = true;
            serverResponse.then().log().all(true).statusCode(HttpStatus.OK.value()).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .body("id", Matchers.is(Matchers.notNullValue(String.class)))
                    .body("partnershipState", Matchers.is("PENDING"))
                    .body("registeredTime", Matchers.isA(String.class));
        }
        else {
            registerSuccess = false;
            serverResponse.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @And("^Confirmation with credential email sent to given business email address \"([^\"]*)\"$")
    public void confirmationWithCredentialEmailSentToGivenBusinessEmailAddress(String emailAddress) throws Throwable {
        if(registerSuccess) {
            MatcherAssert.assertThat(greenMail.getReceivedMessages().length, Matchers.is(1));
            MatcherAssert.assertThat(greenMail.getReceivedMessages()[0].getRecipients(Message.RecipientType.TO)[0].toString(), Matchers.is(emailAddress));
        }
    }
}
