package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class CFTProof implements Serializable {

    private BigInteger C, D1, D2;

    public CFTProof(BigInteger c, BigInteger D1, BigInteger D2) {
        this.C = c;
        this.D1 = D1; // Number that hides the secret value
        this.D2 = D2; // Number that hides the random value in the commitment
    }

    public BigInteger getC() {
        return C;
    }

    public BigInteger getD1() {
        return D1;
    }

    public BigInteger getD2() {
        return D2;
    }
}
