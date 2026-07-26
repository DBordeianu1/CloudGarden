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

class TypeRequestTest {

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
    void shouldAcceptValidType() {
        Set<ConstraintViolation<TypeRequest>> violations = validator.validate(new TypeRequest("Echeveria"));

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectBlankType(String type) {
        Set<ConstraintViolation<TypeRequest>> violations = validator.validate(new TypeRequest(type));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("type");
    }

    @Test
    void shouldReportConfiguredMessageForBlankType() {
        Set<ConstraintViolation<TypeRequest>> violations = validator.validate(new TypeRequest(""));

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("Type is required");
    }
}
