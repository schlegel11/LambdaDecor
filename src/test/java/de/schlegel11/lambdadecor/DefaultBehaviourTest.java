package de.schlegel11.lambdadecor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since VERSION
 */
class DefaultBehaviourTest {

    private Behaviour<String> behaviour;

    @BeforeEach
    void setUp() {
        behaviour = DefaultBehaviour.newBehaviour();
    }

    @Test
    void newBehaviourPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        DefaultBehaviour.<String>newBehaviour(b -> b.with(s -> "Test")
                                                    .with(s -> test.updateAndGet(t -> s))).apply("");
        assertThat(test).hasValue("Test");
        assertThat(behaviour.apply("1")._Behaviour).isEqualTo("1");
    }

    @Test
    void newBehaviourNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> DefaultBehaviour.newBehaviour(null))
                                                             .withMessage("Function is null.");
        assertThat(DefaultBehaviour.newBehaviour(b -> null)).isNull();
    }

    @Test
    void withUnapplyPostive() {
        AtomicReference<String> test = new AtomicReference<>();
        behaviour.withUnapply(s -> () -> test.set("1"))
                 .apply("")._Unapply
                .unapply();
        assertThat(test).hasValue("1");

        test.set("");
        IntStream.rangeClosed(1, 10)
                 .mapToObj(String::valueOf)
                 .forEach(string -> behaviour = behaviour.withUnapply(
                         s -> () -> test.updateAndGet(t -> t.concat(string))));
        behaviour.apply("")._Unapply
                .unapply();
        assertThat(test).hasValue("12345678910");
    }

    @Test
    void withUnapplyNegative() {
        behaviour = behaviour.withUnapply(s -> () -> {
        })
                             .withUnapply(s -> null);
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.apply(""))
                                                             .withMessage("Unappliable is null.");

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.withUnapply(s -> null)
                                                                                        .apply(""))
                                                             .withMessage("Unappliable is null.");

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.withUnapply(null))
                                                             .withMessage("Function is null.");
    }

    @Test
    void withPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        String output = behaviour.with(s -> test.updateAndGet(t -> s))
                                 .apply("1")._Behaviour;
        assertThat(test).hasValue("1");
        assertThat(output).isEqualTo(test.get());

        test.set("");
        IntStream.rangeClosed(1, 10)
                 .mapToObj(String::valueOf)
                 .forEach(string -> behaviour = behaviour.with(
                         s -> {
                             test.updateAndGet(t -> t.concat(s));
                             return string;
                         }));
        output = behaviour.apply("0")._Behaviour;
        assertThat(test).hasValue("0123456789");
        assertThat(output).isEqualTo("10");
    }

    @Test
    void withNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.with(null))
                                                             .withMessage("Function is null.");
    }

    @Test
    void mergePositive() {
        AtomicReference<String> test = new AtomicReference<>();
        Behaviour<String> merge = DefaultBehaviour.newBehaviour(b -> b.with(s -> test.updateAndGet(t -> "1:" + s)));
        String output = behaviour.with(s -> test.updateAndGet(t -> "2:" + s))
                                 .merge(merge)
                                 .merge(merge)
                                 .apply("Test")._Behaviour;
        assertThat(test).hasValue("1:1:2:Test");
        assertThat(output).isEqualTo(test.get());

        test.set("");
        AtomicReference<String> unapplyTest = new AtomicReference<>();
        merge = DefaultBehaviour.newBehaviour(b -> b.with(s -> test.updateAndGet(t -> "1:" + s))
                                                    .withUnapply(s -> () -> unapplyTest.updateAndGet(ut -> ut + "2")));

        Behaviour<String> behaviour2 = DefaultBehaviour.newBehaviour(
                b -> b.withUnapply(s -> () -> unapplyTest.set("1"))
                      .with(s -> test.updateAndGet(t -> "2:" + s)));

        DecorPair<String> decorPair = behaviour.with(s -> test.updateAndGet(t -> "3:" + s))
                                               .merge(behaviour2)
                                               .merge(merge)
                                               .apply("Test");

        assertThat(test).hasValue("1:2:3:Test");
        assertThat(decorPair._Behaviour).isEqualTo(test.get());

        decorPair._Unapply.unapply();
        assertThat(unapplyTest).hasValue("12");
    }

    @Test
    void mergeNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.merge(null))
                                                             .withMessage("Behaviour is null.");
    }

    @Test
    void apply() {
        AtomicReference<String> test = new AtomicReference<>();
        DecorPair<String> decorPair = behaviour.withUnapply(s -> () -> test.set("1"))
                                               .with(Function.identity())
                                               .apply("Test");

        decorPair._Unapply.unapply();
        assertThat(test).hasValue("1");

        assertThat(decorPair._Behaviour).isEqualTo("Test");
        assertThat(DefaultBehaviour.newBehaviour()
                                   .apply(null)._Behaviour).isNull();
    }
}