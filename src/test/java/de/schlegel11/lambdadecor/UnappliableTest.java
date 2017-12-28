package de.schlegel11.lambdadecor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since VERSION
 */
class UnappliableTest {

    @Test
    void andThenPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        Unappliable.EMPTY.andThen(() -> test.updateAndGet(t -> "1"))
                         .andThen(() -> test.updateAndGet(t -> t + "2"))
                         .unapply();

        assertThat(test).hasValue("12");
    }

    @Test
    void andThenNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Unappliable.EMPTY.andThen(null))
                                                             .withMessage("Unappliable is null.");
    }

    @Test
    void allPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        Unappliable.all(() -> test.updateAndGet(t -> "1"), () -> test.updateAndGet(t -> t + "2"))
                   .unapply();

        assertThat(test).hasValue("12");
        assertThat(Unappliable.all()).isEqualTo(Unappliable.EMPTY);
    }

    @Test
    void allNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Unappliable.all(null))
                                                             .withMessage("Unapplies are null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Unappliable.all(null, null))
                                                             .withMessage(
                                                                     "Varargs argument \"unapplies\" in \"all(unapplies)\" contains null.");
    }
}