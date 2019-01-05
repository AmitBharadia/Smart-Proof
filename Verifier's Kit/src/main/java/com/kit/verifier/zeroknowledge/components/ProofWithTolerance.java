package com.kit.verifier.zeroknowledge.components;

import com.kit.verifier.zeroknowledge.dto.*;
import com.kit.verifier.zeroknowledge.exception.ZeroKnowledgeException;
import com.kit.verifier.zeroknowledge.util.BigIntUtil;

import java.math.BigInteger;
import java.security.SecureRandom;

import static com.kit.verifier.zeroknowledge.TTPGenerator.s;
import static java.math.BigInteger.ONE;

/**
 * Implementation of 'Proof with Tolerance'
 * Prove that a committed integer is in range [a - theta, b + theta] with theta = 2^(t+l) 2 sqrt(b-a)
 *
 * This protocol is described in section 3.1 in the following paper:
 * Fabrice Boudot, Efficient Proofs that a Committed Number Lies in an Interval
 */
public class ProofWithTolerance {
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /**
     * @param ttpMessage secret message from the trusted third party
     * @param range the boundaries of the range
     * @return
     */
    public static BoudotRangeProof calculateRangeProof(TTPMessage ttpMessage, ClosedRange range) {

        Commitment commitment = ttpMessage.getCommitment();

        BigInteger N = commitment.getGroup().getN();
        BigInteger g = commitment.getGroup().getG();
        BigInteger h = commitment.getGroup().getH();

        BigInteger a = range.getStart();
        BigInteger b = range.getEnd();
        BigInteger m = ttpMessage.getX(); // number in range
        BigInteger r = ttpMessage.getY(); // commitment key

        System.out.println("X is: "+m);
        System.out.println("Y is: "+r);
        SecureRandom random = new SecureRandom();

        // Step 3
        BigInteger leftSquareRoot = BigIntUtil.floorSquareRoot(m.subtract(a));
        BigInteger rightSquareRoot = BigIntUtil.floorSquareRoot(b.subtract(m));

        BigInteger leftSquare = leftSquareRoot.multiply(leftSquareRoot);
        BigInteger rightSquare = rightSquareRoot.multiply(rightSquareRoot);

        BigInteger leftRemaining = m.subtract(a).subtract(leftSquare);
        BigInteger rightRemaining = b.subtract(m).subtract(rightSquare);

        // Step 4
        BigInteger randomLS = BigIntUtil.randomSignedInt(TWO.pow(s).multiply(N).subtract(ONE), random);
        BigInteger randomRS = BigIntUtil.randomSignedInt(TWO.pow(s).multiply(N).subtract(ONE), random);

        BigInteger randomLR = r.subtract(randomLS);
        BigInteger randomRR = r.negate().subtract(randomRS);

        // Step 5
        BigInteger cLeftSquare = g.modPow(leftSquare, N).multiply(h.modPow(randomLS, N)).mod(N);
        BigInteger cRightSquare = g.modPow(rightSquare, N).multiply(h.modPow(randomRS, N)).mod(N);

        // Step 7
        BigInteger SQ_maxToHide = BigIntUtil.floorSquareRoot(b.subtract(a)).add(ONE);
        SquareProof sqrProofLeft = HPAKESquare.calculateZeroKnowledgeProof(SQ_maxToHide, N, g, h, leftSquareRoot, randomLS, random);
        SquareProof sqrProofRight = HPAKESquare.calculateZeroKnowledgeProof(SQ_maxToHide, N, g, h, rightSquareRoot, randomRS, random);

        // Step 8
        BigInteger CFT_maxCommitment = BigIntUtil.floorSquareRoot(b.subtract(a).shiftLeft(2));
        CFTProof cftProofLeft = CFT.calculateProof(CFT_maxCommitment, N, g, h, leftRemaining, randomLR, random);
        CFTProof cftProofRight = CFT.calculateProof(CFT_maxCommitment, N, g, h, rightRemaining, randomRR, random);

        return new BoudotRangeProof(cLeftSquare, cRightSquare, sqrProofLeft, sqrProofRight, cftProofLeft, cftProofRight);
    }

    public static boolean validateRangeProof(BoudotRangeProof proof, Commitment commitment, ClosedRange range) {

        BigInteger N = commitment.getGroup().getN();
        BigInteger g = commitment.getGroup().getG();
        BigInteger h = commitment.getGroup().getH();
        BigInteger c = commitment.getCommitmentValue();

        BigInteger a = range.getStart();
        BigInteger b = range.getEnd();

        BigInteger cLeftSquare = proof.getCLeftSquare();
        BigInteger cRightSquare = proof.getCRightSquare();

        if (N.equals(BigInteger.ZERO) || c.equals(BigInteger.ZERO) ||
                cLeftSquare.equals(BigInteger.ZERO) || cRightSquare.equals(BigInteger.ZERO)) {
            throw new ZeroKnowledgeException("Invalid input");
        }

        // Step 2
        BigInteger cLeft  = BigIntUtil.divMod(c, g.modPow(a, N), N);
        BigInteger cRight = BigIntUtil.divMod(g.modPow(b, N), c, N);

        // Step 6
        BigInteger cLeftRemaining = BigIntUtil.divMod(cLeft, cLeftSquare, N);
        BigInteger cRightRemaining = BigIntUtil.divMod(cRight, cRightSquare, N);

        // Step 7
        HPAKESquare.validateZeroKnowledgeProof(N, g, h, cLeftSquare, proof.getSqrProofLeft());
        HPAKESquare.validateZeroKnowledgeProof(N, g, h, cRightSquare, proof.getSqrProofRight());

        // Step 8
        BigInteger CFT_maxCommitment = BigIntUtil.floorSquareRoot(b.subtract(a).shiftLeft(2));

        if(CFT_maxCommitment==null) {
            return false;
        }

        CFT.validateZeroKnowledgeProof(CFT_maxCommitment, N, g, h, cLeftRemaining, proof.getCftProofLeft());
        CFT.validateZeroKnowledgeProof(CFT_maxCommitment, N, g, h, cRightRemaining, proof.getCftProofRight());
        return true;
    }
}
