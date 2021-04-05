package com.maxdreher.consumers;

/**
 * SAM, {@link java.util.function.Consumer} but for two items
 *
 * @param <ConsumeA>
 * @param <ConsumeB>
 */
public interface ConsumeTwo<ConsumeA, ConsumeB> {
    void consume(ConsumeA inputA, ConsumeB inputB);
}
