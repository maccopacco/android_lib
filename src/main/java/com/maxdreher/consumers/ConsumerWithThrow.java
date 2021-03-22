package com.maxdreher.consumers;

//TODO #6
public interface ConsumerWithThrow<T> {
    void accept(T t) throws Exception;
}
