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

class NameRequestTest {

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
    void shouldAcceptValidName() {
        Set<ConstraintViolation<NameRequest>> violations = validator.validate(new NameRequest("Snoopy"));

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectBlankName(String name) {
        //whitespace-only is the case that separates @NotBlank from @NotEmpty: the service
        //trims names before saving, so a weaker constraint would persist an empty name
        Set<ConstraintViolation<NameRequest>> violations = validator.validate(new NameRequest(name));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("name");
    }

    @Test
    void shouldReportConfiguredMessageForBlankName() {
        Set<ConstraintViolation<NameRequest>> violations = validator.validate(new NameRequest(""));

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Name is required");
    }
}
