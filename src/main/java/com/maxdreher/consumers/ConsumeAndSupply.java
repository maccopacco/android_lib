package com.maxdreher.consumers;

/**
 * SAM with generics for consuming one item and exporting another
 *
 * @param <Consume>
 * @param <Supply>
 */
public interface ConsumeAndSupply<Consume, Supply> {
    Supply consume(Consume input);
}
