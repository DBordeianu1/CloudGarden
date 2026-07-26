package com.cloudgarden.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SucculentRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        factory.close();
    }

    @Test
    void shouldAcceptValidNameAndType() {
        Set<ConstraintViolation<SucculentRequest>> violations =
                validator.validate(new SucculentRequest("Snoopy", "Echeveria"));

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectBlankNameEvenWhenTypeIsValid(String name) {
        //each constraint has to stand on its own, otherwise one valid field could mask
        //a missing annotation on the other
        Set<ConstraintViolation<SucculentRequest>> violations =
                validator.validate(new SucculentRequest(name, "Echeveria"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectBlankTypeEvenWhenNameIsValid(String type) {
        Set<ConstraintViolation<SucculentRequest>> violations =
                validator.validate(new SucculentRequest("Snoopy", type));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("type");
    }

    @Test
    void shouldReportBothViolationsWhenNameAndTypeAreBlank() {
        Set<ConstraintViolation<SucculentRequest>> violations =
                validator.validate(new SucculentRequest("", ""));

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("Name is required", "Type is required");
    }
}
