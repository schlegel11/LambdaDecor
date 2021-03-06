package de.schlegel11.lambdadecor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
class DefaultLambdaDecorTest {

    private LambdaDecor<String> lambdaDecor;

    @BeforeEach
    void setUp() {
        lambdaDecor = DefaultLambdaDecor.create();
    }

    @Test
    void createPositive() {
        AtomicReference<String> test = new AtomicReference<>();
        DefaultLambdaDecor.<String>create(DefaultBehaviour.newBehaviour(b -> b.with(test::getAndSet)))
                .apply("1");

        assertThat(test).hasValue("1");

        test.set("");
        DefaultLambdaDecor.<String>create(b -> b.with(test::getAndSet))
                .apply("1");

        assertThat(test).hasValue("1");

    }

    @Test
    void createNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> DefaultLambdaDecor.create(
                (Behaviour<Object>) null))
                                                             .withMessage(
                                                                     "Behaviour argument \"behaviour\" for initialisation is null.");

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> DefaultLambdaDecor.create(
                (Function<Behaviour<Object>, Behaviour<Object>>) null))
                                                             .withMessage(
                                                                     "Function is null.");

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> DefaultLambdaDecor.create(b -> null))
                                                             .withMessage(
                                                                     "Behaviour is null.");
    }

    @Test
    void updateBehaviourPositive() {
        AtomicReference<String> test = new AtomicReference<>("2");
        lambdaDecor.updateBehaviour(b -> b.with(test::getAndSet))
                   .updateBehaviour(b -> b.with(s -> test.updateAndGet(t -> t + s)))
                   .apply("1");

        assertThat(test).hasValue("12");
    }

    @Test
    void updateBehaviourNegative() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> lambdaDecor.updateBehaviour(null))
                                                             .withMessage(
                                                                     "Function argument \"behaviourFunction\" in \"updateBehaviour(behaviourFunction)\" is null.");
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> lambdaDecor.updateBehaviour(b -> null))
                                                             .withMessage(
                                                                     "Behaviour is null.");
    }

    @Test
    void apply() {
        assertThat(lambdaDecor.apply("1")).isEqualTo("1");

        AtomicReference<String> test = new AtomicReference<>();
        lambdaDecor.updateBehaviour(b -> b.with(s -> test.updateAndGet(t -> s)));

        assertThat(test).hasValue(lambdaDecor.apply("1"));
    }

    @Test
    void unapply() {
        AtomicReference<String> test = new AtomicReference<>("2");
        lambdaDecor.updateBehaviour(b -> b.withUnapply(s -> () -> test.updateAndGet(t -> t + s)));
        lambdaDecor.unapply();

        assertThat(test).hasValue("2");
        test.set("");

        lambdaDecor.apply("1");
        lambdaDecor.unapply();

        assertThat(test).hasValue("1");
        test.set("");

        lambdaDecor.apply("1");
        lambdaDecor.apply("2");

        lambdaDecor.unapply();

        assertThat(test).hasValue("12");

        lambdaDecor.unapply();

        assertThat(test).hasValue("12");
    }
}