package com.tiket.poc.bdd;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * @author zakyalvan
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features/",
        plugin = {"pretty", "junit:target/junit-report", "html:target/cucumber"},
        glue = {"com.tiket.poc.bdd.feature"},
        strict = true)
public class CucumberRunnerIT {

}
