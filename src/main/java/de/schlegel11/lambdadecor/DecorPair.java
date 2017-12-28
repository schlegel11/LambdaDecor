package de.schlegel11.lambdadecor;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Marcel Schlegel (schlegel11)
 * @since 1.0
 */
public class DecorPair<T> {

    public final T _Behaviour;
    public final Unappliable _Unapply;

    private DecorPair(T _Behaviour, Unappliable _Unapply) {
        this._Behaviour = _Behaviour;
        this._Unapply = _Unapply;
    }

    static <B> DecorPair<B> create(B behaviour, Unappliable unappliable) {
        return new DecorPair<>(behaviour, unappliable);
    }

    DecorPair<T> updateBehaviour(T behaviour) {
        return create(behaviour, _Unapply);
    }

    DecorPair<T> updateUnapply(Function<Unappliable, Unappliable> function) {
        return create(_Behaviour, function.apply(_Unapply));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecorPair<?> decorPair = (DecorPair<?>) o;
        return Objects.equals(_Behaviour, decorPair._Behaviour) &&
                Objects.equals(_Unapply, decorPair._Unapply);
    }

    @Override
    public int hashCode() {

        return Objects.hash(_Behaviour, _Unapply);
    }

    @Override
    public String toString() {
        return "DecorPair{" +
                "_Behaviour=" + _Behaviour +
                ", _Unapply=" + _Unapply +
                '}';
    }
}