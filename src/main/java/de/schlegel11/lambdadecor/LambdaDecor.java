package de.schlegel11.lambdadecor;

import java.util.function.Function;

/**
 * Class that holds a {@link Behaviour} type with a specific type {@link T}.
 *
 * @param <T> type for this behaviour
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
public interface LambdaDecor<T> {

    /**
     * Updates the held {@link Behaviour}.
     *
     * @param behaviour {@link Function} that provides the currently hold {@link Behaviour} and returns the updated {@link Behaviour}
     * @return this {@link LambdaDecor} object
     * @throws NullPointerException if the {@code behaviour} is null or the return value if the {@link Function} is null
     */
    LambdaDecor<T> updateBehaviour(Function<Behaviour<T>, Behaviour<T>> behaviour);

    /**
     * Applies this {@link Behaviour} to the given type {@link T}.
     * The resulting {@link Unappliable} is hold by this {@link LambdaDecor}.
     *
     * @param type specific type {@link T}
     * @return type {@link T} object after applying all {@link Behaviour}s
     */
    T apply(T type);

    /**
     * Performs the specific {@link Unappliable} operation of this {@link Behaviour}.
     * After that operation the {@link Unappliable} should be empty.
     */
    void unapply();
}
