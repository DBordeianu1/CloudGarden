package com.cloudgarden.model;

import com.cloudgarden.repository.SucculentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers the JPA mapping itself, which SucculentTest cannot reach: that @PrePersist
 * is actually invoked, that the status column is written as text, and that the
 * not-null columns are enforced.
 */
@DataJpaTest
class SucculentPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SucculentRepository succulentRepository;

    @Test
    void shouldApplyDefaultsOnPersist() {
        //no status, waterLevel or responseTimeMS set: @PrePersist has to fill them in
        Succulent succulent = new Succulent();
        succulent.setName("Snoopy");
        succulent.setType("Echeveria");

        Succulent saved = entityManager.persistFlushFind(succulent);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(Succulent.Status.HEALTHY);
        assertThat(saved.getWaterLevel()).isEqualTo(100);
        assertThat(saved.getResponseTimeMS()).isEqualTo(50);
    }

    @Test
    void shouldNotOverrideSuppliedValuesOnPersist() {
        Succulent succulent = new Succulent(null, "Woodstock", "Jade Plant", 15, Succulent.Status.WILTING, 850);

        Succulent saved = entityManager.persistFlushFind(succulent);

        assertThat(saved.getStatus()).isEqualTo(Succulent.Status.WILTING);
        assertThat(saved.getWaterLevel()).isEqualTo(15);
        assertThat(saved.getResponseTimeMS()).isEqualTo(850);
    }

    @Test
    void shouldStoreStatusAsTextNotOrdinal() {
        //without @Enumerated(EnumType.STRING) JPA silently stores the ordinal, and
        //every existing row remaps the day someone reorders the enum constants
        Succulent succulent = new Succulent(null, "Lucy", "Aloe", 0, Succulent.Status.ZOMBIE, 3500);
        Succulent saved = entityManager.persistFlushFind(succulent);

        Object storedStatus = entityManager.getEntityManager()
                .createNativeQuery("SELECT status FROM succulents WHERE id = :id")
                .setParameter("id", saved.getId())
                .getSingleResult();

        assertThat(storedStatus).isEqualTo("ZOMBIE");
    }

    @Test
    void shouldRejectNullName() {
        //onCreate() defaults the other columns but never name or type, so the
        //not-null constraint is the only thing standing behind them
        Succulent succulent = new Succulent();
        succulent.setType("Echeveria");

        assertThatThrownBy(() -> succulentRepository.saveAndFlush(succulent))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRejectNullType() {
        Succulent succulent = new Succulent();
        succulent.setName("Snoopy");

        assertThatThrownBy(() -> succulentRepository.saveAndFlush(succulent))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRoundTripEveryStatusValue() {
        //proves each constant survives the write/read cycle, which ordinal mapping
        //would also pass but the native-query test above would not
        for (Succulent.Status status : Succulent.Status.values()) {
            Succulent succulent = new Succulent(null, "Plant " + status, "Cactus", 50, status, 50);

            Succulent saved = entityManager.persistFlushFind(succulent);

            assertThat(saved.getStatus()).isEqualTo(status);
        }
    }
}
