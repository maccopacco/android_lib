package com.maxdreher.consumers;

//TODO #6
public interface ConsumeTwo<ConsumeA, ConsumeB> {
    void consume(ConsumeA inputA, ConsumeB inputB);
}
