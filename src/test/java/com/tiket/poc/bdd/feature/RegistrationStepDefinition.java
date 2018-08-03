package com.tiket.poc.bdd.feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.tiket.poc.SampleApplication;
import com.tiket.poc.dto.RegistrationForm;
import com.tiket.poc.entity.BusinessPartner;
import com.tiket.poc.entity.PartnershipState;
import com.tiket.poc.repo.BusinessPartnerRepository;
import cucumber.api.java8.En;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Ignore
//@SpringBootTest(classes = SampleApplication.class,
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        properties = {"spring.mail.host=localhost", "spring.mail.port=25252",
//                "spring.mail.username=tester", "spring.mail.password=secret"})
//@RunWith(SpringRunner.class)
public class RegistrationStepDefinition /*implements En*/ {
    /*
    @Autowired
    private BusinessPartnerRepository partnerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int boundPort;

    private String organizationName;
    private String contactPerson;
    private String phoneNumber;
    private String emailAddress;

    private String endpointPath;

    private Response serverResponse;

    private boolean registerSuccess = false;

    @Autowired
    private MailProperties mailProperties;

    private GreenMail greenMail;

    public RegistrationStepDefinition() {
        Before(() -> {
            RestAssured.baseURI = UriComponentsBuilder.newInstance()
                    .scheme("http").host("localhost").port(boundPort)
                    .build().toUriString();

            partnerRepository.deleteAll();

            ServerSetup serverSetup = new ServerSetup(mailProperties.getPort(), mailProperties.getHost(), ServerSetup.PROTOCOL_SMTP);
            serverSetup.setServerStartupTimeout(1100);
            greenMail = new GreenMail(serverSetup);
            greenMail.setUser(mailProperties.getUsername(), mailProperties.getPassword());
            greenMail.start();
        });
        Given("^Partner want to register from organization \"([^\"]*)\" contact person \"([^\"]*)\" business phone \"([^\"]*)\" and business email \"([^\"]*)\"$",
                (String organizationName, String contactPerson, String phoneNumber, String emailAddress) -> {

            this.organizationName = organizationName;
            this.contactPerson = contactPerson;
            this.phoneNumber = phoneNumber;
            this.emailAddress = emailAddress;
        });
        And("^Already registered business partners$", (DataTable table) -> {
            // This should be in Background part.
            // For BDD purist, this unacceptable, because we are not treating system under test as black box.
            BusinessPartner businessPartner = BusinessPartner.builder()
                    .id(UUID.randomUUID())
                    .organizationName(table.cell(0, 0))
                    .contactName(table.cell(0, 1))
                    .phoneNumber(table.cell(0, 2))
                    .emailAddress(table.cell(0, 3))
                    .partnershipState(PartnershipState.valueOf(table.cell(0, 4)))
                    .registeredTime(LocalDateTime.now())
                    .build();
            partnerRepository.save(businessPartner);

        });
        And("^Registration endpoint mapped to \"([^\"]*)\"$", (String endpointPath) -> {
            this.endpointPath = endpointPath;
        });
        When("^Submit registration data to registration endpoint$",
                () -> {
            RegistrationForm form = RegistrationForm.builder()
                    .organizationName(organizationName).contactPerson(contactPerson)
                    .businessEmail(emailAddress).businessPhone(phoneNumber)
                    .build();

            this.serverResponse = given().body(objectMapper.writeValueAsString(form))
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .when().log().all(true).post(endpointPath);
        });
        Then("^Partner registered when required data are valid \"([^\"]*)\"$", (String dataValid) -> {
            if(Boolean.parseBoolean(dataValid)) {
                registerSuccess = true;
                serverResponse.then().log().all(true).statusCode(200)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .body("id", is(notNullValue(String.class)))
                        .body("partnershipState", is("PENDING"))
                        .body("registeredTime", notNullValue(String.class));
            }
            else {
                registerSuccess = false;
                serverResponse.then().log().all(true).statusCode(400);
            }
        });
        And("^Confirmation with credential email sent to given business email address \"([^\"]*)\"$",
                (String emailAddress) -> {
            if(registerSuccess) {
                List<MimeMessage> messages = Arrays.asList(greenMail.getReceivedMessages());
                assertThat(messages.size(), is(1));
                assertThat(messages.get(0).getRecipients(Message.RecipientType.TO)[0].toString(), is(emailAddress));
            }
        });
        After(() -> {
            greenMail.stop();
        });

    }
    */
}
