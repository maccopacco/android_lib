package com.maxdreher.consumers;

//TODO #6
public interface ConsumeAndSupply<Consume, Supply> {
    Supply consume(Consume input);
}
