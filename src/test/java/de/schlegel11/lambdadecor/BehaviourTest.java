package de.schlegel11.lambdadecor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since VERSION
 */
class BehaviourTest {

    private Behaviour<String> behaviour;

    @BeforeEach
    void setUp() {
        behaviour = DefaultBehaviour.newBehaviour();
    }

    @Test
    void withAllPositive() {
        DecorPair<String> decorPair = behaviour.withAll(Stream.of(s -> s + "2", s -> s + "3"))
                                               .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("123");

        decorPair = Behaviour.withAll(behaviour, s -> s + "2", s -> s + "3")
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("123");

        decorPair = behaviour.withAll(Stream.of())
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");

        decorPair = Behaviour.withAll(behaviour)
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");
    }

    @Test
    void withAllNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.withAll(null))
                                                             .withMessage(
                                                                     "Stream argument \"functionStream\" in \"withAll(functionStream)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Behaviour.withAll(behaviour, null))
                                                             .withMessage(
                                                                     "Varags \"functions\" in \"withAll(..., functions)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.withAll(Stream.of(null, null)))
                                                             .withMessage(
                                                                     "Stream argument \"functionStream\" in \"withAll(functionStream)\" contains null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Behaviour.withAll(behaviour, null, null))
                                                             .withMessage(
                                                                     "Stream argument \"functionStream\" in \"withAll(functionStream)\" contains null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Behaviour.withAll(null, null))
                                                             .withMessage(
                                                                     "Behaviour argument \"updateBehaviour\" in \"withAll(updateBehaviour, ...)\" is null.");
    }

    @Test
    void withUnapplyAllPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        DecorPair<String> decorPair = behaviour.withUnapplyAll(
                Stream.of(s -> () -> test.updateAndGet(t -> s + "2"), s -> () -> test.updateAndGet(t -> t + "3")))
                                               .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");

        decorPair._Unapply.unapply();
        assertThat(test).hasValue("123");

        test.set("");
        decorPair = Behaviour.withUnapplyAll(behaviour, s -> () -> test.updateAndGet(t -> s + "2"),
                s -> () -> test.updateAndGet(t -> t + "3"))
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");

        decorPair._Unapply.unapply();
        assertThat(test).hasValue("123");

        decorPair = behaviour.withUnapplyAll(Stream.of())
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");
        assertThat(decorPair._Unapply).isNotNull();

        decorPair = Behaviour.withUnapplyAll(behaviour)
                             .apply("1");
        assertThat(decorPair._Behaviour).isEqualTo("1");
        assertThat(decorPair._Unapply).isNotNull();
    }

    @Test
    void withUnapplyAllNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.withUnapplyAll(null))
                                                             .withMessage(
                                                                     "Stream argument \"functionStream\" in \"withUnapplyAll(functionStream)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.withUnapplyAll(behaviour, null))
                                                             .withMessage(
                                                                     "Varags \"functions\" in \"withUnapplyAll(..., functions)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> behaviour.withUnapplyAll(Stream.of(null, null)))
                                                             .withMessage(
                                                                     "Function is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.withUnapplyAll(behaviour, null, null))
                                                             .withMessage(
                                                                     "Function is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.withUnapplyAll(null, null))
                                                             .withMessage(
                                                                     "Behaviour argument \"updateBehaviour\" in \"withUnapplyAll(updateBehaviour, ...)\" is null.");
    }

    @Test
    void mergeAllPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        Behaviour<String> merge = DefaultBehaviour.newBehaviour(b -> b.with(s -> test.updateAndGet(t -> "1:" + s)));
        String output = behaviour.with(s -> test.updateAndGet(t -> "2:" + s))
                                 .mergeAll(Stream.of(merge, merge))
                                 .apply("Test")._Behaviour;

        assertThat(test).hasValue("1:1:2:Test");
        assertThat(output).isEqualTo(test.get());

        test.set("");
        output = Behaviour.mergeAll(behaviour.with(s -> test.updateAndGet(t -> "2:" + s)), merge, merge)
                          .apply("Test")._Behaviour;

        assertThat(test).hasValue("1:1:2:Test");
        assertThat(output).isEqualTo(test.get());
    }

    @Test
    void mergeAllNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> behaviour.mergeAll(null))
                                                             .withMessage(
                                                                     "Stream argument \"behaviourStream\" in \"mergeAll(behaviourStream)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.mergeAll(behaviour, null))
                                                             .withMessage(
                                                                     "Varags \"behaviours\" in \"mergeAll(..., behaviours)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> behaviour.mergeAll(Stream.of(null, null)))
                                                             .withMessage(
                                                                     "Behaviour is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.mergeAll(behaviour, null, null))
                                                             .withMessage(
                                                                     "Behaviour is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
                () -> Behaviour.mergeAll(null, null))
                                                             .withMessage(
                                                                     "Behaviour argument \"updateBehaviour\" in \"mergeAll(updateBehaviour, ...)\" is null.");
    }
}