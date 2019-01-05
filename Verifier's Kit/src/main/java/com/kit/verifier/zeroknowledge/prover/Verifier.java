package com.kit.verifier.zeroknowledge.prover;

import com.kit.verifier.zeroknowledge.HyperLedger.HyperLedgerClient;
import com.kit.verifier.zeroknowledge.RangeProof;
import com.kit.verifier.zeroknowledge.dto.*;
import com.kit.verifier.zeroknowledge.util.ExportUtil;
import com.kit.verifier.zeroknowledge.util.InputUtils;

import java.math.BigInteger;
import java.util.Scanner;

public class Verifier {

    public boolean runValidation(String lowerBound, String upperBound, String fileName) {

        ClosedRange range = getRange(lowerBound, upperBound);

        System.out.println("Reading commitment from trusted 3rd party");

        String fileLocation = Config.getInstance().getProperty("upload.location");

        System.out.println("Filename is: "+ fileLocation + fileName);

        TTPMessage ttpMessage = (TTPMessage) InputUtils.readObject(fileLocation+fileName);
        Commitment commitment = ttpMessage.getCommitment();

        System.out.println("the value of x is: "+ttpMessage.getX());
        System.out.println("Range is: "+range);
        BoudotRangeProof rangeProof = RangeProof.calculateRangeProof(ttpMessage, range);

        return validateOnHyperledger(rangeProof, commitment, range) && validateJava(rangeProof, commitment, range);
    }

    private boolean validateJava(BoudotRangeProof rangeProof, Commitment commitment, ClosedRange range) {

        try {
            if(RangeProof.validateRangeProof(rangeProof, commitment, range)) {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateOnHyperledger(BoudotRangeProof rangeProof, Commitment commitment, ClosedRange range) {

        HyperLedgerClient client = HyperLedgerClient.getEthereumClient();

        if (!client.validate(range.getStart(), range.getEnd(),
                ExportUtil.exportForEVM(commitment), ExportUtil.exportForEVM(rangeProof, commitment, range))) {
            System.err.println("Range proof validation failed");
            return false;
        }

        return true;
    }

    private static ClosedRange getRange(String lowerBound, String upperBound) {

        BigInteger lo, hi;

        lo = new BigInteger(lowerBound);
        hi = new BigInteger(upperBound);

        return ClosedRange.of(lo, hi);
    }
}