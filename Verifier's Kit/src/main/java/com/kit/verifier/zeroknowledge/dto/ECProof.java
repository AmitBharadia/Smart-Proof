package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class ECProof implements Serializable {
    private BigInteger c, D, D1, D2;

    public ECProof(BigInteger c, BigInteger D, BigInteger D1, BigInteger D2) {
        this.c = c;   // Challenge for prover
        this.D = D;   // Number that hides the secret value (proved to be the same in both commitments)
        this.D1 = D1; // Number that hides the random value in commitment 1
        this.D2 = D2; // Number that hides the random value in commitment 2
    }

    public BigInteger getC() {
        return c;
    }

    public BigInteger getD() {
        return D;
    }

    public BigInteger getD1() {
        return D1;
    }

    public BigInteger getD2() {
        return D2;
    }
}