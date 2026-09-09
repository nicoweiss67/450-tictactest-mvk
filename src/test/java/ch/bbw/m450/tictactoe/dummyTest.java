package ch.bbw.m450.tictactoe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dummy-Test, der nur belegt, dass JUnit und AssertJ im Projekt korrekt
 * eingebunden sind und ausgeführt werden können.
 */
class DummySetupTest {

    @Test
    void junitIstEingerichtet() {
        assertTrue(true);
    }

    @Test
    void assertJIstEingerichtet() {
        assertThat(true).isTrue();
    }
}