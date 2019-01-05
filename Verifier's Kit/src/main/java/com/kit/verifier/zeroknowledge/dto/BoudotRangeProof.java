package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class BoudotRangeProof implements Serializable {

    private SquareProof sqrProofLeft, sqrProofRight;
    private CFTProof cftProofLeft, cftProofRight;
    private BigInteger cLeftSquare, cRightSquare;

    public BoudotRangeProof(BigInteger cLeftSquare, BigInteger cRightSquare, SquareProof sqrProofLeft,
                            SquareProof sqrProofRight, CFTProof cftProofLeft, CFTProof cftProofRight) {
        this.cLeftSquare = cLeftSquare;
        this.cRightSquare = cRightSquare;
        this.sqrProofLeft = sqrProofLeft;
        this.sqrProofRight = sqrProofRight;
        this.cftProofLeft = cftProofLeft;
        this.cftProofRight = cftProofRight;
    }

    public SquareProof getSqrProofLeft() {
        return sqrProofLeft;
    }

    public SquareProof getSqrProofRight() {
        return sqrProofRight;
    }

    public CFTProof getCftProofLeft() {
        return cftProofLeft;
    }

    public CFTProof getCftProofRight() {
        return cftProofRight;
    }

    public BigInteger getCLeftSquare() {
        return cLeftSquare;
    }

    public BigInteger getCRightSquare() {
        return cRightSquare;
    }
}
