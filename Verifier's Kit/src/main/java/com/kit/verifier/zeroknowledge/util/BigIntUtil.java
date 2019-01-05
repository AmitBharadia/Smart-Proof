package com.kit.verifier.zeroknowledge.util;

import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntUtil {

    private static final int DEFAULT_DIGITS_TO_SHOW = 4;

    private BigIntUtil() {
        throw new UnsupportedOperationException("utility class");
    }

    public static String shortBigInt(final BigInteger n, final int showDigits) {
        final String s = n.abs().toString();
        final String sign = n.signum() < 0 ? "-" : "";
        final int numChars = s.length();
        if (numChars <= (2 * showDigits)) {
            return String.format("%s%s", sign, s);
        } else {
            return String.format("%s%s…%s",
                    sign, s.substring(0, showDigits), s.substring(numChars - showDigits, numChars));
        }
    }

    public static String shortBigInt(final BigInteger n) {
        return shortBigInt(n, DEFAULT_DIGITS_TO_SHOW);
    }

    // Babylonian method for approximating square root
    private static BigInteger approximateSquareRoot(BigInteger input) {
        if (input.compareTo(BigInteger.ONE) == -1) {
            return BigInteger.ZERO;
        }

        BigInteger prev2 = BigInteger.ZERO;
        BigInteger prev = BigInteger.ZERO;
        BigInteger approx = BigInteger.ZERO.setBit(input.bitLength() / 2);

        // To improve approximation of a = sqrt(i), set it to (i / a + a) / 2
        // Stop when approx does not change or starts alternating
        while (!approx.equals(prev) && !approx.equals(prev2)) {
            prev2 = prev;
            prev = approx;
            approx = input.divide(approx).add(approx).shiftRight(1);
        }
        return approx;
    }

    public static BigInteger floorSquareRoot(BigInteger input) {
        if (input.compareTo(BigInteger.ZERO) < 0) {
            //throw new IllegalArgumentException("Negative number does not have square root");
            return null;
        }
        BigInteger res = approximateSquareRoot(input);
        if (res.multiply(res).compareTo(input) >= 1) {
            res = res.subtract(BigInteger.ONE);
        }
        return res;
    }

    public static BigInteger divMod(BigInteger a, BigInteger b, BigInteger N) {
        return a.multiply(b.modInverse(N)).mod(N);
    }

    public static BigInteger randomSignedInt(BigInteger maxAbsoluteValue, SecureRandom random) {
        return BigIntegers.createRandomInRange(maxAbsoluteValue.negate(), maxAbsoluteValue, random);
    }

    public static int roundUpToMultiple(int i, int N) {
        return (i + (N - 1)) / N * N;
    }
}
