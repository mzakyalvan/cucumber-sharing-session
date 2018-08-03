#Sharing Session

## Introduction

> Testing : far beyond green bar or sonar coverage, but deployment and production confidence.

### BDD Brief

BDD is extension of TDD, which write test failing test first, then implement system and/or components under tests, make the tests passes and iterate.

BDD is set of practices, not testing tools. So Cucumber, JBehave etc are not BDD by their self but tools supporting BDD.

BDD work best in iterative context. Everyone know, writing complete requirements upfront is hard.


### Challenge

- Require high collaboration between product owner or user, developer and qa engineer.
- BDD wont work well in silo organization (Product owner speaks in vague requirements, developer write jira stories and develop the features and then qa team come in the last phase, product owners found written features don't match with their expectation then blame game start).

## Hands On

Now, time to get our hands dirty. But for technical aspect only, which are transform your features or scenarios executable tests.

We will use partner registration for b2b integration case for sample.

### Prepare Project

Open [start.spring.io](https://start.spring.io/), choose latest spring boot version 1.5.x, key in group id (e.g. ```com.tiket.poc```) and artifact name (e.g. ```cucumber-sharing-session```) then search and add ```web```, ```mongodb```, ```embedded mongodb``` (for testing purpose) and ```lombok```, ```mail``` (sending email). Generate the project and download process will start. You will get following initial ```pom.xml``` content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.tiket.poc</groupId>
    <artifactId>cucumber-sharing-session</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>cucumber-sharing-session</name>
    <description>Demo project for Cucumber BDD</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.15.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>de.flapdoodle.embed</groupId>
            <artifactId>de.flapdoodle.embed.mongo</artifactId>
            <version>2.0.3</version>
            <scope>test</scope>
        </dependency>  
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

```

Add additional dependencies for [Jackson support for Java 8](https://github.com/FasterXML/jackson-modules-java8). This module and datatype will be auto-configured by spring boot.

```xml
            <dependency>
                <groupId>com.fasterxml.jackson.module</groupId>
                <artifactId>jackson-module-parameter-names</artifactId>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.datatype</groupId>
                <artifactId>jackson-datatype-jdk8</artifactId>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.datatype</groupId>
                <artifactId>jackson-datatype-jsr310</artifactId>
            </dependency>
```

Then add following [RxJava](https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava) (based on our current tools), [Rest Assured](https://mvnrepository.com/artifact/io.rest-assured/rest-assured), [Cucumbers](https://mvnrepository.com/artifact/io.cucumber/cucumber-java), [Awaitility](https://mvnrepository.com/artifact/org.awaitility/awaitility) and [GreenMail](https://mvnrepository.com/artifact/com.icegreen/greenmail) test scoped dependencies

```xml
            <dependency>
                <groupId>io.reactivex.rxjava2</groupId>
                <artifactId>rxjava</artifactId>
                <version>2.1.17</version>
            </dependency>          

            <dependency>
                <groupId>io.rest-assured</groupId>
                <artifactId>rest-assured</artifactId>
                <version>3.1.0</version>
                <scope>test</scope>
            </dependency>

            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-java</artifactId>
                <version>3.0.2</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-java8</artifactId>
                <version>3.0.2</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-spring</artifactId>
                <version>3.0.2</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>io.cucumber</groupId>
                <artifactId>cucumber-junit</artifactId>
                <version>3.0.2</version>
                <scope>test</scope>
            </dependency>
            
            <dependency>
                <groupId>org.awaitility</groupId>
                <artifactId>awaitility</artifactId>
                <version>3.1.1</version>
                <scope>test</scope>
            </dependency>
    
            <dependency>
                <groupId>com.icegreen</groupId>
                <artifactId>greenmail</artifactId>
                <version>1.5.7</version>
                <scope>test</scope>
            </dependency>

```

> Using embedded MongoDb database simplify our testing, no need to maintain state of database between each test execution of our step definitions.

Configure [```maven-surefire-plugin<```](https://maven.apache.org/surefire/maven-surefire-plugin/index.html) and [```maven-failsafe-plugin```](https://maven.apache.org/surefire/maven-failsafe-plugin/) plugin to separate unit tests and integration tests execution.

```xml

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                    </includes>
                    <excludes>
                        <exclude>**/*IT.java</exclude>
                        <exclude>**/*ITest.java</exclude>
                        <exclude>**/*ITests.java</exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*IT.class</include>
                        <include>**/*ITest.class</include>
                        <include>**/*ITests.class</include>
                    </includes>
                </configuration>
            </plugin>

```

### Features and Stories

> Jetbrains officially provide Cucumber plugins to assists us when writing feature files and generating step definitions stubs. Install them to simplify workflow.

Story must be written in abstract, explain what, not how you done the tests or how your system or feature implemented.

Following are sample of our Gherkin feature file, stored in ```src/test/resources/registration.feature``` file.

```gherkin

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

```

> Please note, data in ```Examples``` must be provided, no step definition run when no row data given. 

### Write Test

Cucumber requiring a test runner, which responsible to execute all of step definition (will be explained later).

```java
package com.tiket.poc.bdd;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
        plugin = {"pretty", "junit:target/junit-report", "html:target/cucumber"},
        glue = {"com.tiket.poc.bdd.feature"},
        strict = true)
public class CucumberRunnerIT {
}

```

Simple, just an empty junit test class. You might be wondering why this type name prefixed with ```IT```. Look at ```maven-failsafe-plugin``` configuration above. Remember, nature Acceptance is falling inside integration test.

First line (```@RunWith(Cucumber.class)```) instruct to run the test using ```cucumber.api.junit.Cucumber``` junit runner. For ```cucumber.api.CucumberOptions```, its javadocs explain more.

```java
package cucumber.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation provides the same options as the cucumber command line, {@link cucumber.api.cli.Main}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CucumberOptions {
    /**
     * @return true if this is a dry run
     */
    boolean dryRun() default false;

    /**
     * @return true if strict mode is enabled (fail if there are undefined or pending steps)
     */
    boolean strict() default false;

    /**
     * @return the uris to the feature(s)
     */
    String[] features() default {};

    /**
     * @return where to look for glue code (stepdefs and hooks)
     */
    String[] glue() default {};

    /**
     * @return what tags in the features should be executed
     */
    String[] tags() default {};

    /**
     * @return what plugins(s) to use
     */
    String[] plugin() default {};

    /**
     * @return whether or not to use monochrome output
     */
    boolean monochrome() default false;

    /**
     * Specify a patternfilter for features or scenarios
     *
     * @return a list of patterns
     */
    String[] name() default {};

    /**
     * @return what format should the snippets use. underscore, camelcase
     */
    SnippetType snippets() default SnippetType.UNDERSCORE;

    /**
     * @return the options for the JUnit runner
     */
    String[] junit() default {};

}

```

Next step, based on scenario, write your test step definition.

> Use IntelliJ's Cucumber plugin to be more productive! Assist on writing feature files and create stub test also track your scenarios to step definition class.

Following is stub cucumber step definition generated, augment using custom annotations.

```java
package com.tiket.poc.bdd.feature;

import com.tiket.poc.SampleApplication;
import cucumber.api.PendingException;
import cucumber.api.java8.En;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@SpringBootTest(classes = SampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RegistrationStepDefinition implements En {
    public RegistrationStepDefinition() {
        Given("^Partner want to register from organization \"([^\"]*)\" contact person \"([^\"]*)\" business phone \"([^\"]*)\" and business email \"([^\"]*)\"$",
                (String organizationName, String contactPerson, String phoneNumber, String emailAddress) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });
        And("^Registration endpoint bound to \"([^\"]*)\"$", (String endpointPath) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });
        When("^Submit registration data to registration endpoint$", () -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });
        Then("^Partner registered$", () -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });
        And("^Confirmation with credential email sent to given business email address \"([^\"]*)\"$", (String emailAddress) -> {
            // Write code here that turns the phrase above into concrete actions
            throw new PendingException();
        });
    }
}

```

Annotation ```@org.junit.Ignore``` prevent this type to be accidentally picked up by JUnit test infrastructure. Next annotation is just spring boot's testing annotation.

This step definition generated with Java 8 style (lambda). You can also define each step using method augment using cucumber's annotation.

```java


```

Lets complete our step definition.

> Please note, each row data in ```Examples``` of ```Scenario Outline``` given in Gherkin file will be executed in different instance of step definition. Means, it is safe to share state for different steps using instance properties.

```java
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
@SpringBootTest(classes = SampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.mail.host=localhost", "spring.mail.port=25252",
                "spring.mail.username=tester", "spring.mail.password=secret"})
@RunWith(SpringRunner.class)
public class RegistrationStepDefinition implements En {
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
}

```

> We provide ```spring.mail.host=localhost```, ```spring.mail.port=25252```, ```spring.mail.username=tester``` and ```spring.mail.password=secret``` as settings for smtp server access. The first one required to enable ```MailSenderAutoConfiguration``` which responsible for creating ```JavaMailSender``` bean.

## Implement Features

> By using BDD practices, we implement features in top-to-bottom approach (in term of spring's layering architecture recommended practices).

Create controller for handling partner registration.

```java

package com.tiket.poc.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@RestController
@RequestMapping("/bitubi/registry")
public class RegistrationController {
    @PostMapping
    public DeferredResult<Object> handleRegistration(@Validated @RequestBody Object form, BindingResult bindings) {
        DeferredResult<Object> deferred = new DeferredResult<>();
        return deferred;
    }
}
```

Create data transfer object modelling request body

> I'm using [lombok](https://projectlombok.org/) intensively to hide code verbosity.

```java

package com.tiket.poc.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@Getter
@JsonDeserialize(builder = RegistrationForm.RegistrationFormBuilder.class)
@SuppressWarnings("serial")
public class RegistrationForm implements Serializable {
    @NotBlank
    private String organizationName;

    @NotBlank
    private String contactPerson;

    @Email
    @NotBlank
    private String businessEmail;

    @NotBlank
    private String businessPhone;

    @Builder
    protected RegistrationForm(String organizationName, String contactPerson, String businessEmail, String businessPhone) {
        this.organizationName = organizationName;
        this.contactPerson = contactPerson;
        this.businessEmail = businessEmail;
        this.businessPhone = businessPhone;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RegistrationFormBuilder {

    }
}

```

And response body

```java
package com.tiket.poc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.tiket.poc.entity.PartnershipState;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonDeserialize(builder = RegistrationSummary.RegistrationSummaryBuilder.class)
@SuppressWarnings("serial")
public class RegistrationSummary implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    private PartnershipState partnershipState;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registeredTime;

    @Builder
    protected RegistrationSummary(UUID id, PartnershipState partnershipState, LocalDateTime registeredTime) {
        this.id = id;
        this.partnershipState = partnershipState;
        this.registeredTime = registeredTime;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RegistrationSummaryBuilder {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public RegistrationSummaryBuilder registeredTime(LocalDateTime registeredTime) {
            this.registeredTime = registeredTime;
            return this;
        }
    }
}


```

Update method signature of your Controller's registration handler method, also add registration logic

```java
package com.tiket.poc.rest;

import com.tiket.poc.dto.RegistrationSummary;
import com.tiket.poc.dto.RegistrationForm;
import com.tiket.poc.service.RegistrationService;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.schedulers.Schedulers;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/bitubi/registry")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Handle partner registration.
     *
     * @param form
     * @param bindings
     * @return
     */
    @PostMapping
    DeferredResult<RegistrationSummary> handleRegistration(@Validated @RequestBody RegistrationForm form, BindingResult bindings) {
        Single<RegistrationSummary> summary = Single.just(bindings)
                .compose(extraValidations(form, bindings))
                .flatMap(errors -> errors.hasErrors() ?
                        Single.error(new DataBindingException(errors)) :
                        registrationService.registerPartner(registration -> registration.organizationName(form.getOrganizationName())
                                        .contactPerson(form.getContactPerson())
                                        .businessEmail(form.getBusinessEmail())
                                        .phoneNumber(form.getBusinessPhone()))
                                .map(partner -> RegistrationSummary.builder()
                                        .id(partner.getId())
                                        .partnershipState(partner.getPartnershipState())
                                        .registeredTime(partner.getRegisteredTime())
                                        .build()));

        DeferredResult<RegistrationSummary> deferred = new DeferredResult<>();
        summary.subscribe(deferred::setResult, deferred::setErrorResult);
        return deferred;
    }

    /**
     * Extra validation for submitted partner data.
     *
     * @param form
     * @param bindings
     * @return
     */
    private SingleTransformer<Errors, Errors> extraValidations(RegistrationForm form, BindingResult bindings) {
        Single<Boolean> registeredEmail = Single.just(bindings)
                .flatMap(errors -> errors.hasFieldErrors("businessEmail") ?
                        Single.just(false) :
                        registrationService.hasPartner(form.getBusinessEmail()).subscribeOn(Schedulers.io()));

        return upstream -> upstream.zipWith(registeredEmail, (errors, registered) -> {
            if(registered) {
                errors.rejectValue("businessEmail", "already.registered", "Given business email address already registered");
            }
            return errors;
        });
    }
}
```

With assumption we already write following service type

```java

package com.tiket.poc.service;

import com.tiket.poc.entity.BusinessPartner;
import io.reactivex.Single;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.function.Consumer;

public interface RegistrationService {
    Single<BusinessPartner> registerPartner(@NotNull Consumer<RegistrationData> customizer);
    Single<Boolean> hasPartner(String businessEmail);

    @Data
    @Accessors(fluent = true, chain = true)
    @SuppressWarnings("serial")
    class RegistrationData implements Serializable {
        private String organizationName;
        private String contactPerson;
        private String businessEmail;
        private String phoneNumber;
    }
}

```

With its default implementation backed by MongoDb storage

```java

package com.tiket.poc.service;

import com.tiket.poc.entity.BusinessPartner;
import com.tiket.poc.entity.PartnershipState;
import com.tiket.poc.outbound.EventMailSender;
import com.tiket.poc.outbound.MailBuilder;
import com.tiket.poc.outbound.RegistrationMailBuilder;
import com.tiket.poc.repo.BusinessPartnerRepository;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Validated
public class DefaultRegistrationService implements RegistrationService {
    private final BusinessPartnerRepository partnerRepository;

    private EventMailSender mailSender;

    public DefaultRegistrationService(BusinessPartnerRepository partnerRepository) {
        Assert.notNull(partnerRepository, "Partner repository must be provided");
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Single<BusinessPartner> registerPartner(Consumer<RegistrationData> customizer) {
        return Single.just(customizer)
                .map(consumer -> {
                    RegistrationData registration = new RegistrationData();
                    consumer.accept(registration);

                    return BusinessPartner.builder()
                            .id(UUID.randomUUID())
                            .organizationName(registration.organizationName())
                            .contactName(registration.contactPerson())
                            .partnershipState(PartnershipState.PENDING)
                            .emailAddress(registration.businessEmail())
                            .phoneNumber(registration.phoneNumber())
                            .registeredTime(LocalDateTime.now())
                            .build();
                })
                .flatMap(partner -> Single.fromCallable(() -> partnerRepository.save(partner))
                        .subscribeOn(Schedulers.io()))
                .flatMap(partner -> {
                    MailBuilder mail = new RegistrationMailBuilder()
                            .recipientAddress(partner.getEmailAddress())
                            .organizationId(partner.getId().toString())
                            .organizationName(partner.getOrganizationName())
                            .contactPerson(partner.getContactName())
                            .partnershipState(partner.getPartnershipState())
                            .registeredTime(partner.getRegisteredTime());

                    return mailSender.send(mail)
                            // I don't want to break all flow because of mail sending failure.
                            // We can publish an event as failure sign using "onErrorResumeNext" but don't break the flow.
                            //.onErrorComplete()
                            .andThen(Single.just(partner));
                });
    }

    @Override
    public Single<Boolean> hasPartner(String businessEmail) {
        return Single.fromCallable(() -> {
            if(StringUtils.hasText(businessEmail)) {
                boolean exists = partnerRepository.exists(Example.of(BusinessPartner.builder().emailAddress(businessEmail).build()));
                return exists;
            }
            return false;
        });
    }

    @Autowired(required = false)
    public void setMailSender(EventMailSender mailSender) {
        this.mailSender = mailSender;
    }
}

```

And entity type to store business data

```java
package com.tiket.poc.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "business-partner")
public class BusinessPartner implements Persistable<UUID>, Serializable {
    @Id
    private UUID id;

    @NotBlank
    private String organizationName;

    @NotBlank
    private String contactName;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String emailAddress;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private PartnershipState partnershipState;

    @NotNull
    private LocalDateTime registeredTime;

    @Transient
    private boolean alreadyRegistered = true;

    @Builder
    protected BusinessPartner(UUID id, String organizationName, String contactName, String emailAddress, String phoneNumber, PartnershipState partnershipState, @NotNull LocalDateTime registeredTime) {
        this.id = id;
        this.organizationName = organizationName;
        this.contactName = contactName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.partnershipState = partnershipState;
        this.registeredTime = registeredTime;
        this.alreadyRegistered = false;
    }

    @Override
    public boolean isNew() {
        return !alreadyRegistered;
    }
}

```

Enumeration to represent state of business partnership

```java
package com.tiket.poc.entity;

public enum PartnershipState {
    PENDING,
    CANCELED,
    ACTIVE,
    BLACKLISTED
}

```

MongoDb repository contract for ```BusinessPartner``` entity simply extend ```org.springframework.data.mongodb.repository.MongoRepository```

```java

package com.tiket.poc.repo;

import com.tiket.poc.entity.BusinessPartner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BusinessPartnerRepository extends MongoRepository<BusinessPartner, UUID> {
}

```

Outbound component contract, which responsible for sending email to registered partner

```java
package com.tiket.poc.outbound;

import io.reactivex.Completable;

import javax.validation.constraints.NotNull;

public interface EventMailSender {
    Completable send(@NotNull MailBuilder builder);
}

```

Default implementation of ```EventMailSender```

```java
package com.tiket.poc.outbound;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import static io.reactivex.Completable.fromAction;

@Component
@Validated
class DefaultEventMailSender implements EventMailSender {
    private final JavaMailSender mailSender;

    public DefaultEventMailSender(JavaMailSender mailSender) {
        Assert.notNull(mailSender, "Mail sender object must be provided");
        this.mailSender = mailSender;
    }

    @Override
    public Completable send(MailBuilder builder) {
        return Single.just(builder)
                .map(MailBuilder::build)
                .flatMapCompletable(preparer -> fromAction(() -> mailSender.send(preparer)).subscribeOn(Schedulers.io()));
    }
}

```

Also contract ```MailBuilder``` used in parameter of ```EventMailSender.send```

```java
package com.tiket.poc.outbound;

import org.springframework.mail.javamail.MimeMessagePreparator;

public interface MailBuilder {
    MailBuilder recipientAddress(String emailAddress);
    MimeMessagePreparator build();
}

```

The last, ```MailBuilder``` implementation to build partner registration mail

```java
package com.tiket.poc.outbound;

import com.tiket.poc.entity.PartnershipState;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Accessors(fluent = true, chain = true)
public class RegistrationMailBuilder implements MailBuilder {
    private String recipientAddress;
    private String organizationId;
    private String organizationName;
    private String contactPerson;
    private PartnershipState partnershipState;
    private LocalDateTime registeredTime;

    @Override
    public MimeMessagePreparator build() {
        return message -> {
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("technology@tiket.com");
            helper.setTo(recipientAddress);
            helper.setSubject("Welcome to tiket.com");

            String content = String.format("Hi %s, Welcome to tiket.com. Your account registered on %s",
                    organizationName,
                    registeredTime.format(DateTimeFormatter.ISO_DATE_TIME));
            helper.setText(content, true);
        };
    }
}

```

## Advance Topics

Recommendation for next sessions or research by yourself.

### Life Documentation

Use Serenity for better documentation.

### Integrate With Jira

Integrate the workflow with Jira to maximize collaboration. Based on feature meetings or discussions, features written in Jira, feature files selectively download by plugin into projects, developer write tests and implement features, commit to vcs, trigger build in ci, iterate.
