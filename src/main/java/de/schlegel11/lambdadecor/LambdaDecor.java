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
     * @throws NullPointerException if the {@code behaviour} is null or the return value if the {@link Function} is null
     */
    void updateBehaviour(Function<Behaviour<T>, Behaviour<T>> behaviour);

    /**
     * Applies this {@link Behaviour} to the given type {@link T}.
     *
     * @param type specific type {@link T}
     * @return type {@link T} object after applying all {@link Behaviour}s
     */
    T apply(T type);

    /**
     * Performs the specific {@link Unappliable} operation of this {@link Behaviour}.
     */
    void unapply();
}
