package de.schlegel11.lambdadecor;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
public class DefaultLambdaDecor<T> implements LambdaDecor<T> {

    private Behaviour<T> behaviour;
    private Unappliable unappliable = Unappliable.EMPTY;

    private DefaultLambdaDecor(Behaviour<T> behaviour) {
        this.behaviour = Objects.requireNonNull(behaviour,
                "Behaviour argument \"updateBehaviour\" for initialisation is null.");
    }

    public static <T> LambdaDecor<T> create(Behaviour<T> behaviour) {
        return new DefaultLambdaDecor<>(behaviour);
    }

    public static <T> LambdaDecor<T> create(Function<Behaviour<T>, Behaviour<T>> behaviourFunction) {
        return create(Objects.requireNonNull(DefaultBehaviour.newBehaviour(behaviourFunction), "Behaviour is null."));
    }

    public static <T> LambdaDecor<T> create() {
        return create(DefaultBehaviour.newBehaviour());
    }

    @Override
    public LambdaDecor<T> updateBehaviour(Function<Behaviour<T>, Behaviour<T>> behaviourFunction) {
        Objects.requireNonNull(behaviourFunction,
                "Function argument \"behaviourFunction\" in \"updateBehaviour(behaviourFunction)\" is null.");
        behaviour = Objects.requireNonNull(behaviourFunction.apply(behaviour), "Behaviour is null.");
        return this;
    }

    @Override
    public T apply(T type) {
        DecorPair<T> pair = behaviour.apply(type);
        unappliable = pair._Unapply;
        return pair._Behaviour;
    }

    @Override
    public void unapply() {
        unappliable.unapply();
    }
}