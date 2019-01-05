package com.kit.prover.zeroknowledge.prover;

import com.kit.prover.zeroknowledge.TTPGenerator;
import com.kit.prover.zeroknowledge.dto.TTPMessage;
import com.kit.prover.zeroknowledge.util.InputUtils;

import java.math.BigInteger;

/**
 * Generates the commitment
 */
public class TTPDemo {

    public void generateTrustedMessage(BigInteger x, String fileName) {

        String newFileName = fileName.substring(0, fileName.length()-4);

        System.out.println("New file in TTPDemo is: "+newFileName);
        TTPMessage message = TTPGenerator.generateTTPMessage(x);

        String filePath = Config.getInstance().getProperty("upload.location");
        String rangeProofExtension = Config.getInstance().getProperty("rangeProofExtension");
        InputUtils.saveObject(filePath + newFileName + rangeProofExtension, message);

    }
}