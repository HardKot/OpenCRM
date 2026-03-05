package com.open.crm.core.application.common;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Result<T, E> {

    private T value;

    private E error;

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            return success(mapper.apply(value));
        }
        else {
            return failure(error);
        }
    }

    public <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
        if (isFailure()) {
            return failure(mapper.apply(error));
        }
        else {
            return success(value);
        }
    }

    public void ifSuccess(Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(value);
        }
    }

    public void ifFailure(Consumer<? super E> action) {
        if (isFailure()) {
            action.accept(error);
        }
    }

    public T getValue() {
        if (isSuccess()) {
            return value;
        }
        else {
            throw new IllegalStateException("Cannot get value from a failure result");
        }
    }

    public E getError() {
        if (isFailure()) {
            return error;
        }
        else {
            throw new IllegalStateException("Cannot get error from a success result");
        }
    }

}
