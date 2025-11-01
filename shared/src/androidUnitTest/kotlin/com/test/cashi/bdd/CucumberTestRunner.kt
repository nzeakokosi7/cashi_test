package com.test.cashi.bdd

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

/**
 * Cucumber test runner for BDD scenarios
 *
 * This runner executes all feature files in the resources/features directory
 * No Firebase required - uses mocked repository!
 *
 * Run with: ./gradlew :shared:testDebugUnitTest --tests "CucumberTestRunner"
 */
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],  // Use classpath: prefix for resources
    glue = ["com.test.cashi.bdd.steps"],
    plugin = [
        "pretty",
        "html:build/reports/cucumber/cucumber-report.html",
        "json:build/reports/cucumber/cucumber-report.json"
    ],
    monochrome = true,
    publish = false  // Don't publish results to cucumber.io
)
class CucumberTestRunner