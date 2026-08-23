package com.cloudgarden.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the hand-written behaviour on the entity: the @PrePersist defaults.
 * onCreate() is protected, so this test lives in the same package to call it directly.
 * That the callback is actually invoked by JPA is covered in SucculentPersistenceTest.
 */
class SucculentTest {

    @Test
    void shouldApplyEveryDefaultForABareEntity() {
        Succulent succulent = new Succulent();

        succulent.onCreate();

        assertThat(succulent.getStatus()).isEqualTo(Succulent.Status.HEALTHY);
        assertThat(succulent.getWaterLevel()).isEqualTo(100);
        assertThat(succulent.getResponseTimeMS()).isEqualTo(50);
    }

    @Test
    void shouldDefaultOnlyTheMissingField() {
        //a partially populated entity must keep what it was given
        Succulent succulent = new Succulent();
        succulent.setWaterLevel(30);
        succulent.setResponseTimeMS(850);

        succulent.onCreate();

        assertThat(succulent.getStatus()).isEqualTo(Succulent.Status.HEALTHY);
        assertThat(succulent.getWaterLevel()).isEqualTo(30);
        assertThat(succulent.getResponseTimeMS()).isEqualTo(850);
    }

    @ParameterizedTest
    @EnumSource(Succulent.Status.class)
    void shouldNotOverwriteAnExplicitStatus(Succulent.Status status) {
        //guards against the null checks being dropped: a DEAD plant must not be
        //resurrected to HEALTHY on the way into the database
        Succulent succulent = new Succulent();
        succulent.setStatus(status);

        succulent.onCreate();

        assertThat(succulent.getStatus()).isEqualTo(status);
    }

    @Test
    void shouldPreserveZeroWaterLevel() {
        //0 is a meaningful value, not a missing one: it is exactly the state of a
        //plant the scheduler has just killed
        Succulent succulent = new Succulent();
        succulent.setWaterLevel(0);

        succulent.onCreate();

        assertThat(succulent.getWaterLevel()).isZero();
    }

    @Test
    void shouldPreserveZeroResponseTime() {
        Succulent succulent = new Succulent();
        succulent.setResponseTimeMS(0);

        succulent.onCreate();

        assertThat(succulent.getResponseTimeMS()).isZero();
    }

    @Test
    void shouldLeaveNameAndTypeAlone() {
        //onCreate() deliberately does not default these, they are caller-supplied
        //and enforced by the not-null columns instead
        Succulent succulent = new Succulent();

        succulent.onCreate();

        assertThat(succulent.getName()).isNull();
        assertThat(succulent.getType()).isNull();
    }
}
