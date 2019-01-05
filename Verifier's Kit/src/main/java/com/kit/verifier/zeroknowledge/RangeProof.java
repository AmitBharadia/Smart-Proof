package com.kit.verifier.zeroknowledge;

import com.kit.verifier.zeroknowledge.components.CFT;
import com.kit.verifier.zeroknowledge.components.ProofWithTolerance;
import com.kit.verifier.zeroknowledge.dto.*;
import com.kit.verifier.zeroknowledge.exception.ZeroKnowledgeException;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

public class RangeProof {
    /**
     * @param ttpMessage secret message from the trusted third party
     * @param range the boundaries of the range
     * @return
     */
    public static BoudotRangeProof calculateRangeProof(TTPMessage ttpMessage, ClosedRange range) {

        // Step 1 - scale commitment
        // Original value should be up to 256 bits
        // Scaled value can be up to 850 bits
        int T = 2 * (CFT.t + CFT.l + 1) + (range.getEnd().subtract(range.getStart())).bitLength();
        BigInteger xPrime = ttpMessage.getX().shiftLeft(T);
        BigInteger yPrime = ttpMessage.getY().shiftLeft(T);
        SecretOrderGroup group = ttpMessage.getCommitment().getGroup();

        Commitment cPrime = TTPGenerator.commit(group, xPrime, yPrime);
        TTPMessage ttpMessagePrime = new TTPMessage(cPrime, xPrime, yPrime);

        // Step 2 - Prove that scaled commitment
        ClosedRange rangePrime = ClosedRange.of(range.getStart().shiftLeft(T), range.getEnd().shiftLeft(T));

        return ProofWithTolerance.calculateRangeProof(ttpMessagePrime, rangePrime);
    }

    public static boolean validateRangeProof(BoudotRangeProof proof, Commitment commitment, ClosedRange range) throws ZeroKnowledgeException {
        int T = 2 * (CFT.t + CFT.l + 1) + (range.getEnd().subtract(range.getStart())).bitLength();
        SecretOrderGroup group = commitment.getGroup();

        BigInteger cPrime = commitment.getCommitmentValue().modPow(ONE.shiftLeft(T), group.getN());
        ClosedRange rangePrime = ClosedRange.of(range.getStart().shiftLeft(T), range.getEnd().shiftLeft(T));

        if(ProofWithTolerance.validateRangeProof(proof, new Commitment(group, cPrime), rangePrime)) {
            return true;
        }

        return false;
    }
}
