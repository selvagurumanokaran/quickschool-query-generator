package com.quickschools.model;

import java.util.Objects;

public class Join<S extends Field<?>, T extends Field<?>> {
    S primaryKeyFirst;
    T primaryKeySecond;

    public Join(S primaryKeyFirst, T primaryKeySecond) {
        this.primaryKeyFirst = primaryKeyFirst;
        this.primaryKeySecond = primaryKeySecond;
    }

    public S getPrimaryKeyFirst() {
        return primaryKeyFirst;
    }

    public T getPrimaryKeySecond() {
        return primaryKeySecond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Join<?, ?> join = (Join<?, ?>) o;

        if (!Objects.equals(primaryKeyFirst, join.primaryKeyFirst))
            return false;
        return Objects.equals(primaryKeySecond, join.primaryKeySecond);
    }

    @Override
    public int hashCode() {
        int result = primaryKeyFirst != null ? primaryKeyFirst.hashCode() : 0;
        result = 31 * result + (primaryKeySecond != null ? primaryKeySecond.hashCode() : 0);
        return result;
    }
}
