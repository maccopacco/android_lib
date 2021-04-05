package com.maxdreher.consumers;

/**
 * Combination of as {@link ConsumeAndSupply} and {@link ConsumeTwo}
 *
 * @param <ConsumeA>
 * @param <ConsumeB>
 * @param <Supply>
 */
public interface ConsumeTwoAndSupply<ConsumeA, ConsumeB, Supply> {
    Supply consume(ConsumeA inputA, ConsumeB inputB);
}
