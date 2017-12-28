package de.schlegel11.lambdadecor;

import java.util.Arrays;
import java.util.Objects;

/**
 * Interface which provides a {@link FunctionalInterface}.
 * <br>
 * The {@link Unappliable} is a simple {@link FunctionalInterface} with no argument and no return value.
 * <p>This is a {@link FunctionalInterface}
 * whose functional method is {@link #unapply()}.
 *
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
@FunctionalInterface
public interface Unappliable {

    /**
     * An empty {@link Unappliable}.
     */
    Unappliable EMPTY = () -> {
    };

    /**
     * Accepts a vararg of {@link Unappliable} objects and
     * returns a composed {@link Unappliable} that performs, in sequence,
     * all operations.
     *
     * @param unapplies the {@link Unappliable} objects
     * @return a composed {@link Unappliable} that performs, in sequence, all operations
     * @throws NullPointerException if {@code unapplies} is null or if vararg {@code unapplies} contains null elements
     */
    static Unappliable all(Unappliable... unapplies) {
        Objects.requireNonNull(unapplies, "Unapplies are null.");
        return Arrays.stream(unapplies)
                     .map(u -> Objects.requireNonNull(u,
                             "Varargs argument \"unapplies\" in \"all(unapplies)\" contains null."))
                     .reduce(Unappliable::andThen)
                     .orElse(EMPTY);
    }

    /**
     * Performs a specific operation.
     */
    void unapply();

    /**
     * Returns a composed {@link Unappliable} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@link Unappliable} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Unappliable andThen(Unappliable after) {
        Objects.requireNonNull(after, "Unappliable is null.");
        return () -> {
            unapply();
            after.unapply();
        };
    }
}