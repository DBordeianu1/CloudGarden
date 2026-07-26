package com.cloudgarden.dto;

import com.cloudgarden.model.Succulent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SucculentResponseTest {

    @Test
    void shouldMapEveryFieldFromEntity() {
        //waterLevel and responseTimeMS are both Integer, so a positional mix-up in the
        //constructor call would still compile: distinct values are what catch it
        Succulent succulent = new Succulent(7L, "Snoopy", "Echeveria", 42, Succulent.Status.WILTING, 850);

        SucculentResponse response = SucculentResponse.fromEntity(succulent);

        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getName()).isEqualTo("Snoopy");
        assertThat(response.getType()).isEqualTo("Echeveria");
        assertThat(response.getWaterLevel()).isEqualTo(42);
        assertThat(response.getResponseTimeMS()).isEqualTo(850);
    }

    @ParameterizedTest
    @EnumSource(Succulent.Status.class)
    void shouldConvertStatusEnumToItsName(Succulent.Status status) {
        Succulent succulent = new Succulent(1L, "Snoopy", "Echeveria", 100, status, 50);

        SucculentResponse response = SucculentResponse.fromEntity(succulent);

        assertThat(response.getStatus()).isEqualTo(status.name());
    }

    @Test
    void shouldMapNullIdForUnsavedEntity() {
        Succulent succulent = new Succulent(null, "Snoopy", "Echeveria", 100, Succulent.Status.HEALTHY, 50);

        SucculentResponse response = SucculentResponse.fromEntity(succulent);

        assertThat(response.getId()).isNull();
    }

    @Test
    void shouldThrowWhenStatusIsNull() {
        //documents current behaviour: fromEntity calls status.name() with no null guard,
        //and status is only defaulted by @PrePersist, so an entity that has not been
        //persisted yet blows up here
        Succulent succulent = new Succulent(1L, "Snoopy", "Echeveria", 100, null, 50);

        assertThatThrownBy(() -> SucculentResponse.fromEntity(succulent))
                .isInstanceOf(NullPointerException.class);
    }
}
