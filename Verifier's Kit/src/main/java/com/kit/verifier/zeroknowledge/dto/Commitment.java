package com.kit.verifier.zeroknowledge.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class Commitment implements Serializable {
    private SecretOrderGroup group;
    private BigInteger commitmentValue;

    public Commitment (SecretOrderGroup group, BigInteger commitmentValue) {
        this.group = group;
        this.commitmentValue = commitmentValue;
    }

    public SecretOrderGroup getGroup() {
        return group;
    }

    public BigInteger getCommitmentValue() {
        return commitmentValue;
    }
}
