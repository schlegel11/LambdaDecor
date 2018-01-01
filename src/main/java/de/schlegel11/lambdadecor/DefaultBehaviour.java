package de.schlegel11.lambdadecor;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
public class DefaultBehaviour<T> implements Behaviour<T> {

    private final Function<DecorPair<T>, DecorPair<T>> behaviour;

    private DefaultBehaviour() {
        this(Function.identity());
    }

    private DefaultBehaviour(Function<DecorPair<T>, DecorPair<T>> behaviour) {
        this.behaviour = Objects.requireNonNull(behaviour, "Behaviour is null.");
    }

    /**
     * Creates a new instance.
     *
     * @param function the {@link Function} that provides an new {@link DefaultBehaviour} instance and returns one.
     * @param <T>      type for this behaviour
     * @return the new instance
     * @throws NullPointerException if the {@code function} is null
     */
    public static <T> Behaviour<T> newBehaviour(final Function<Behaviour<T>, Behaviour<T>>
                                                        function) {
        Objects.requireNonNull(function, "Function is null.");
        return function.apply(newBehaviour());
    }

    /**
     * Creates a new instance.
     *
     * @param <T> type for this behaviour
     * @return the new instance
     */
    public static <T> Behaviour<T> newBehaviour() {
        return new DefaultBehaviour<>();
    }

    public final Behaviour<T> withUnapply(final Function<T, Unappliable> function) {
        Objects.requireNonNull(function, "Function is null.");
        return new DefaultBehaviour<>(
                behaviour.andThen(t -> t.updateUnapply(u -> u.andThen(function.apply(t._Behaviour)))));
    }

    public final Behaviour<T> with(final Function<T, T> function) {
        Objects.requireNonNull(function, "Function is null.");
        return new DefaultBehaviour<>(behaviour.andThen(t -> t.updateBehaviour(function.apply(t._Behaviour))));
    }

    public final Behaviour<T> merge(final Behaviour<T> other) {
        Objects.requireNonNull(other, "Behaviour is null.");
        return new DefaultBehaviour<>(behaviour.andThen(p -> {
            DecorPair<T> pair = other.apply(p._Behaviour);
            return p.updateBehaviour(pair._Behaviour)
                    .updateUnapply(u -> u.andThen(pair._Unapply));
        }));
    }

    public final DecorPair<T> apply(final T type) {
        return behaviour.apply(DecorPair.create(type, Unappliable.EMPTY));
    }
}