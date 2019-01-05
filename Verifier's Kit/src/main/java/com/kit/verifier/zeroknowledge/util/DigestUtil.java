package com.kit.verifier.zeroknowledge.util;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

public class DigestUtil {
    private DigestUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static void update(Digest digest, BigInteger ... bigIntegers) {
        for (BigInteger bigInt : bigIntegers) {
            int lengthInBytes = BigIntUtil.roundUpToMultiple(bigInt.bitLength(), 256) / 8;
            byte[] encodedBigInt = BigIntegers.asUnsignedByteArray(lengthInBytes, bigInt);
            update(digest, encodedBigInt);
            Arrays.fill(encodedBigInt, (byte) 0);
        }
    }

    public static BigInteger calculateHash(BigInteger ... bigIntegers) {
        Digest digest = new KeccakDigest(256);
        DigestUtil.update(digest, bigIntegers);

        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal(output, 0);
        return BigIntegers.fromUnsignedByteArray(output);
    }

    private static void update(Digest digest, byte[] buffer) {
        digest.update(buffer, 0, buffer.length);
    }
}
