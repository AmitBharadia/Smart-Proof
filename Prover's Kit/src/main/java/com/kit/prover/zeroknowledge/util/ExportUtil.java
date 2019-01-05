package com.kit.prover.zeroknowledge.util;

import com.kit.prover.zeroknowledge.dto.BoudotRangeProof;
import com.kit.prover.zeroknowledge.dto.ClosedRange;
import com.kit.prover.zeroknowledge.dto.Commitment;
import com.kit.prover.zeroknowledge.dto.SecretOrderGroup;
import com.kit.prover.zeroknowledge.components.CFT;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class ExportUtil {

    // Old CSV/ASCII encoding for Go validation

    /**
     * Returns the commitment as an string representing an array of big integers.
     */
    private static String exportCSV(Commitment commitment) {
        return toCSV(toList(commitment));
    }

    private static String exportCSV(BoudotRangeProof p) {
        return toCSV(toList(p));
    }

    // New bigint-array encoding for EVM validator

    public static byte[] exportForEVM(Commitment commitment) {
        return toSolidityBytes(toRedundantList(commitment));
    }

    public static byte[] exportForEVM(BoudotRangeProof p, Commitment commitment, ClosedRange range) {
        return toSolidityBytes(toRedundantList(p, commitment, range));
    }

    // Helper methods

    public static String toCSV(List<BigInteger> ints) {
        int bitlength = 0;
        StringBuilder proofString = new StringBuilder();
        for (int i = 0; i < ints.size(); i++) {
            if (i > 0) {
                proofString.append(',');
            }
            bitlength += ints.get(i).bitLength();
            proofString.append(ints.get(i).toString());
        }
        System.out.println("Range Proof bitlength = " + bitlength);
        return proofString.toString();
    }


    private static List<BigInteger> toList(Commitment commitment) {
        List<BigInteger> ints = new ArrayList<>();
        ints.add(commitment.getCommitmentValue());
        ints.add(commitment.getGroup().getN());
        ints.add(commitment.getGroup().getG());
        ints.add(commitment.getGroup().getH());
        return ints;
    }

    private static List<BigInteger> toRedundantList(Commitment commitment) {
        List<BigInteger> ints = toList(commitment);
        ints.add(commitment.getCommitmentValue().modInverse(commitment.getGroup().getN()));
        ints.add(commitment.getGroup().getG().modInverse(commitment.getGroup().getN()));
        ints.add(commitment.getGroup().getH().modInverse(commitment.getGroup().getN()));
        return ints;
    }

    private static List<BigInteger> toList(BoudotRangeProof p) {
        List<BigInteger> ints = new ArrayList<>();
        ints.add(p.getCLeftSquare());
        ints.add(p.getCRightSquare());
        ints.add(p.getSqrProofLeft().getF());
        ints.add(p.getSqrProofLeft().getECProof().getC());
        ints.add(p.getSqrProofLeft().getECProof().getD());
        ints.add(p.getSqrProofLeft().getECProof().getD1().abs());
        ints.add(p.getSqrProofLeft().getECProof().getD2().abs());
        ints.add(p.getSqrProofRight().getF());
        ints.add(p.getSqrProofRight().getECProof().getC());
        ints.add(p.getSqrProofRight().getECProof().getD());
        ints.add(p.getSqrProofRight().getECProof().getD1().abs());
        ints.add(p.getSqrProofRight().getECProof().getD2().abs());
        ints.add(p.getCftProofLeft().getC());
        ints.add(p.getCftProofLeft().getD1());
        ints.add(p.getCftProofLeft().getD2().abs());
        ints.add(p.getCftProofRight().getC());
        ints.add(p.getCftProofRight().getD1());
        ints.add(p.getCftProofRight().getD2().abs());
        return ints;
    }

    public static List<BigInteger> toRedundantList(BoudotRangeProof p, Commitment commitment, ClosedRange range) {
        List<BigInteger> ints = toList(p);
        // Redundant information
        SecretOrderGroup group = commitment.getGroup();
        ints.add(p.getCLeftSquare().modInverse(group.getN()));
        ints.add(p.getCRightSquare().modInverse(group.getN()));
        ints.add(p.getSqrProofLeft().getF().modInverse(group.getN()));
        ints.add(p.getSqrProofRight().getF().modInverse(group.getN()));

        int T = 2 * (CFT.t + CFT.l + 1) + (range.getEnd().subtract(range.getStart())).bitLength();
        BigInteger cPrime = commitment.getCommitmentValue().modPow(ONE.shiftLeft(T), group.getN());
        ClosedRange rangePrime = ClosedRange.of(range.getStart().shiftLeft(T), range.getEnd().shiftLeft(T));

        BigInteger cLeft  = BigIntUtil.divMod(cPrime, group.getG().modPow(rangePrime.getStart(), group.getN()), group.getN());
        BigInteger cRight = BigIntUtil.divMod(group.getG().modPow(rangePrime.getEnd(), group.getN()), cPrime, group.getN());

        ints.add(BigIntUtil.divMod(cLeft, p.getCLeftSquare(), group.getN()).modInverse(group.getN()));
        ints.add(BigIntUtil.divMod(cRight, p.getCRightSquare(), group.getN()).modInverse(group.getN()));

        ints.add(p.getSqrProofLeft().getECProof().getD1().signum() == -1 ? ONE : CFT.TWO);
        ints.add(p.getSqrProofLeft().getECProof().getD2().signum() == -1 ? ONE : CFT.TWO);
        ints.add(p.getSqrProofRight().getECProof().getD1().signum() == -1 ? ONE : CFT.TWO);
        ints.add(p.getSqrProofRight().getECProof().getD2().signum() == -1 ? ONE : CFT.TWO);
        ints.add(p.getCftProofLeft().getD2().signum() == -1 ? ONE : CFT.TWO);
        ints.add(p.getCftProofRight().getD2().signum() == -1 ? ONE : CFT.TWO);

        ints.add(BigIntUtil.floorSquareRoot(rangePrime.getEnd().subtract(rangePrime.getStart()).shiftLeft(2)));

        return ints;
    }

    // Workaround for Solidity, arrays of variable-length items not supported for input parameters

    private static final int EVM_WORD_SIZE = 32;
    private static final int INDEX_ITEM_SIZE = 32;
    private static final int BITS_PER_BYTE = 8;

    public static byte[] toSolidityBytes(List<BigInteger> ints) {
        // Build index containing the start positions
        int[] index = new int[ints.size() + 1];
        int position = INDEX_ITEM_SIZE * ints.size();
        for (int i = 0; i < ints.size(); i++) {
            index[i] = position;
            //position += roundUpToMultiple(ints.get(i).bitLength(), 256);
            // Round bit length up to an integer number of bytes
            position += (ints.get(i).bitLength() + (BITS_PER_BYTE - 1)) / BITS_PER_BYTE;
            // Round up to multiple of 32 bytes
            position = (position + (EVM_WORD_SIZE - 1)) / EVM_WORD_SIZE * EVM_WORD_SIZE;
        }
        // Last index contains end of data
        index[index.length - 1] = position;

        // Write index and bigint list to output
        byte[] output = new byte[position];
        for (int i = 0; i < ints.size(); i++) {
            copyField(output, i * INDEX_ITEM_SIZE, INDEX_ITEM_SIZE, BigInteger.valueOf(index[i]));
        }
        for (int i = 0; i < ints.size(); i++) {
            copyField(output, index[i], index[i+1] - index[i], ints.get(i));
        }
        return output;
    }

    private static void copyField(byte[] output, int offset, int length, BigInteger value) {
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Unable to encode negative value");
        }
        byte[] bigIntBytes = BigIntegers.asUnsignedByteArray(length, value);
        System.arraycopy(bigIntBytes, 0, output, offset, length);
    }


}
