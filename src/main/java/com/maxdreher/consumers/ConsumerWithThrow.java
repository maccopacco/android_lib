package com.maxdreher.consumers;

/**
 * Same as {@link java.util.function.Consumer}, but with throw
 *
 * @param <T>
 */
public interface ConsumerWithThrow<T> {
    void accept(T t) throws Exception;
}
