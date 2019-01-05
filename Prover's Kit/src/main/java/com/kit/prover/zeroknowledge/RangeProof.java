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

package com.kit.prover.zeroknowledge;

import com.kit.prover.zeroknowledge.components.ProofWithTolerance;
import com.kit.prover.zeroknowledge.dto.*;
import com.kit.prover.zeroknowledge.exception.ZeroKnowledgeException;
import com.kit.prover.zeroknowledge.components.CFT;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

/**
 * Implementation of 'Proof without Tolerance'
 *
 * This protocol is described in section 3.2 in the following paper:
 * Fabrice Boudot, Efficient Proofs that a Committed Number Lies in an Interval
 */
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

    public static void validateRangeProof(BoudotRangeProof proof, Commitment commitment, ClosedRange range) throws ZeroKnowledgeException {
        int T = 2 * (CFT.t + CFT.l + 1) + (range.getEnd().subtract(range.getStart())).bitLength();
        SecretOrderGroup group = commitment.getGroup();

        BigInteger cPrime = commitment.getCommitmentValue().modPow(ONE.shiftLeft(T), group.getN());
        ClosedRange rangePrime = ClosedRange.of(range.getStart().shiftLeft(T), range.getEnd().shiftLeft(T));

        ProofWithTolerance.validateRangeProof(proof, new Commitment(group, cPrime), rangePrime);
    }
}
