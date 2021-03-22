package com.maxdreher.consumers;

//TODO #6
public interface ConsumeTwoAndSupply<ConsumeA, ConsumeB, Supply> {
    Supply consume(ConsumeA inputA, ConsumeB inputB);
}
