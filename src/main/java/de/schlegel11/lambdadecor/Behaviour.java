package de.schlegel11.lambdadecor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class which provides functions for a {@link Behaviour}.
 * A {@link Behaviour} is internal defined as a {@link Function} that accepts and returns a specific type {@link T}.
 * Furthermore a {@link Behaviour} can handle a {@link Function} that accepts a specific type {@link T} and returns a {@link Unappliable}.
 * A {@link Behaviour} implementation should be immutable.
 *
 * @param <T> type for this behaviour
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
public interface Behaviour<T> {

    /**
     * Adds a {@link Function} that accepts and returns a specific type {@link T}.
     * <br>
     * The returned type {@link T} is passed to the next {@link Function}.
     *
     * @param function {@link Function} that accepts and returns a specific type {@link T}
     * @return new {@link Behaviour} with added {@link Function}
     * @throws NullPointerException if function is null
     */
    Behaviour<T> with(final Function<T, T> function);

    /**
     * Add all {@link Function}s contained by the {@link Stream}.
     * <p>
     * For more see {@link #with(Function)}.
     *
     * @param functionStream {@link Stream} containing {@link Function}s
     * @return new {@link Behaviour} with added {@link Function}s
     * @throws NullPointerException if functionStream is null or
     *                              if a {@link Function} contained by the {@link Stream} is null
     */
    default Behaviour<T> withAll(final Stream<Function<T, T>> functionStream) {
        Objects.requireNonNull(functionStream,
                "Stream argument \"functionStream\" in \"withAll(functionStream)\" is null.");

        return with(functionStream.map(f -> Objects.requireNonNull(f,
                "Stream argument \"functionStream\" in \"withAll(functionStream)\" contains null."))
                                  .reduce(Function::andThen)
                                  .orElse(Function.identity()));
    }

    /**
     * Add all {@link Function}s contained by the {@link Function} vararg.
     * <p>
     * For more see {@link #withAll(Stream)}.
     *
     * @param behaviour {@link Behaviour} whose {@link #withAll(Stream)} method is called
     * @param functions {@link Function} vararg
     * @param <T>       type for this behaviour
     * @return new {@link Behaviour} with added {@link Function}s
     * @throws NullPointerException If behaviour is null or
     *                              if functions is null
     */
    @SafeVarargs
    static <T> Behaviour<T> withAll(final Behaviour<T> behaviour, final Function<T, T>... functions) {
        Objects.requireNonNull(behaviour,
                "Behaviour argument \"updateBehaviour\" in \"withAll(updateBehaviour, ...)\" is null.");
        Objects.requireNonNull(functions, "Varags \"functions\" in \"withAll(..., functions)\" is null.");

        return behaviour.withAll(Arrays.stream(functions));
    }

    /**
     * Adds a {@link Function} that accepts a specific type {@link T} and returns an {@link Unappliable}.
     * <br>
     * The accepted type {@link T} is passed from the last {@link Function} with a return type {@link T}.
     *
     * @param function {@link Function} that accepts the specific type {@link T} and returns an {@link Unappliable}
     * @return new {@link Behaviour} with added {@link Function}
     * @throws NullPointerException if function is null
     */
    Behaviour<T> withUnapply(final Function<T, Unappliable> function);

    /**
     * Add all {@link Function}s contained by the {@link Stream}.
     * <p>
     * For more see {@link #withUnapply(Function)}.
     *
     * @param functionStream {@link Stream} containing {@link Function}s
     * @return new {@link Behaviour} with added {@link Function}s
     * @throws NullPointerException if functionStream is null
     */
    default Behaviour<T> withUnapplyAll(final Stream<Function<T, Unappliable>> functionStream) {
        Objects.requireNonNull(functionStream,
                "Stream argument \"functionStream\" in \"withUnapplyAll(functionStream)\" is null.");

        Behaviour<T> behaviour = this;
        for (Function<T, Unappliable> f : functionStream.collect(Collectors.toList())) {
            behaviour = behaviour.withUnapply(f);
        }
        return behaviour;
    }

    /**
     * Add all {@link Function}s contained by the {@link Function} vararg.
     * <p>
     * For more see {@link #withUnapplyAll(Stream)}.
     *
     * @param behaviour {@link Behaviour} whose {@link #withUnapplyAll(Stream)} method is called
     * @param functions {@link Function} vararg
     * @param <T>       type for this behaviour
     * @return new {@link Behaviour} with added {@link Function}s
     * @throws NullPointerException if behaviour is null or
     *                              if functions is null
     */
    @SafeVarargs
    static <T> Behaviour<T> withUnapplyAll(final Behaviour<T> behaviour, final Function<T, Unappliable>... functions) {
        Objects.requireNonNull(behaviour,
                "Behaviour argument \"updateBehaviour\" in \"withUnapplyAll(updateBehaviour, ...)\" is null.");
        Objects.requireNonNull(functions, "Varags \"functions\" in \"withUnapplyAll(..., functions)\" is null.");

        return behaviour.withUnapplyAll(Arrays.stream(functions));
    }

    /**
     * Merges two {@link Behaviour}s into a new {@link Behaviour} instance.
     * The merged {@code behaviour} is performed in sequence.
     *
     * @param behaviour {@link Behaviour} that is merged into this {@link Behaviour}
     * @return new merged {@link Behaviour}
     * @throws NullPointerException if behaviour is null
     */
    Behaviour<T> merge(final Behaviour<T> behaviour);

    /**
     * Merge all {@link Behaviour}s contained by the {@link Stream}.
     * <p>
     * For more see {@link #merge(Behaviour)}.
     *
     * @param behaviourStream {@link Stream} containing {@link Behaviour}s
     * @return new merged {@link Behaviour}
     * @throws NullPointerException if behaviourStream is null
     */
    default Behaviour<T> mergeAll(final Stream<Behaviour<T>> behaviourStream) {
        Objects.requireNonNull(behaviourStream,
                "Stream argument \"behaviourStream\" in \"mergeAll(behaviourStream)\" is null.");

        return behaviourStream.reduce(this, Behaviour::merge);
    }

    /**
     * Add all {@link Behaviour}s contained by the {@link Behaviour} vararg.
     * <p>
     * For more see {@link #withUnapplyAll(Stream)}.
     *
     * @param behaviour  {@link Behaviour} whose {@link #mergeAll(Stream)} method is called
     * @param behaviours {@link Behaviour} vararg
     * @param <T>        type for this behaviour
     * @return new merged {@link Behaviour}
     * @throws NullPointerException if behaviour is null or
     *                              if behaviours is null
     */
    @SafeVarargs
    static <T> Behaviour<T> mergeAll(final Behaviour<T> behaviour, final Behaviour<T>... behaviours) {
        Objects.requireNonNull(behaviour,
                "Behaviour argument \"updateBehaviour\" in \"mergeAll(updateBehaviour, ...)\" is null.");
        Objects.requireNonNull(behaviours, "Varags \"behaviours\" in \"mergeAll(..., behaviours)\" is null.");

        return behaviour.mergeAll(Arrays.stream(behaviours));
    }

    /**
     * Applies this {@link Behaviour} to the given type {@link T}.
     *
     * @param type specific type {@link T}
     * @return {@link DecorPair} with the type {@link T} object after applying all {@link Function}s and an {@link Unappliable}
     */
    DecorPair<T> apply(final T type);
}