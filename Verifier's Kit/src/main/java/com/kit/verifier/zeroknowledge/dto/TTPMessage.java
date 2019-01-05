package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class TTPMessage implements Serializable {

    private final Commitment commitment;
    private final BigInteger y;
    private final BigInteger x;


    public TTPMessage(Commitment commitment, BigInteger x, BigInteger y) {
        this.commitment = commitment;
        this.y = y;
        this.x = x;
    }

    public Commitment getCommitment() {
        return commitment;
    }

    /**
     * Returns the number to hide (e.g. the age) called x in the whitepaper.
     * @return x, the number to hide.
     */
    public BigInteger getX() {
        return x;
    }

    /**
     * Returns the secret key called y in the whitepaper.
     * @return y, the secret key.
     */
    public BigInteger getY() {
        return y;
    }
}
