package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;


public class SquareProof implements Serializable {

    // Commitment to the root of the square (x)
    private BigInteger F;

    // Proof that two commitments hide the same value x
    private ECProof ecProof;

    public SquareProof(BigInteger F, ECProof ecProof) {
        this.F = F;
        this.ecProof = ecProof;
    }

    public BigInteger getF() {
        return F;
    }

    public ECProof getECProof() {
        return ecProof;
    }
}
