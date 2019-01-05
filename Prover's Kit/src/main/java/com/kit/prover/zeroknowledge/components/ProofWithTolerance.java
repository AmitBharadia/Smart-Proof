/*
 * Copyright 2017 ING Bank N.V.
 * This file is part of the go-ethereum library.
 *
 * The go-ethereum library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The go-ethereum library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the go-ethereum library. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.kit.prover.zeroknowledge.components;

import com.kit.prover.zeroknowledge.dto.*;
import com.kit.prover.zeroknowledge.exception.ZeroKnowledgeException;
import com.kit.prover.zeroknowledge.util.BigIntUtil;

import java.math.BigInteger;
import java.security.SecureRandom;

import static com.kit.prover.zeroknowledge.TTPGenerator.s;
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

    public static void validateRangeProof(BoudotRangeProof proof, Commitment commitment, ClosedRange range) {

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
        CFT.validateZeroKnowledgeProof(CFT_maxCommitment, N, g, h, cLeftRemaining, proof.getCftProofLeft());
        CFT.validateZeroKnowledgeProof(CFT_maxCommitment, N, g, h, cRightRemaining, proof.getCftProofRight());
    }
}
