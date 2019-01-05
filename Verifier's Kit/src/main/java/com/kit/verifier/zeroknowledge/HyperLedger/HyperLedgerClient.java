package com.kit.verifier.zeroknowledge.HyperLedger;

import com.kit.verifier.zeroknowledge.prover.Config;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class HyperLedgerClient {

    private final Web3j web3j;

    public HyperLedgerClient(final String ethereumUrl) {
        web3j = Web3j.build(new HttpService(ethereumUrl));
    }

    public static HyperLedgerClient getHyperledgerClient() {
        final String hyperLedgerUrl = Config.getInstance().getProperty("hyperledger.url");
        return new HyperLedgerClient(hyperLedgerUrl);
    }

    public boolean validate(BigInteger lowerBound, BigInteger upperBound, byte[] commitment, byte[] proof) {
        try {
            Credentials credentials = getCredentials();

            System.out.println("Deploying validator, sender = " + getAddress());
            RangeProofValidator rpv = RangeProofValidator.deploy(web3j, credentials).sendAsync().get(40, TimeUnit.SECONDS);
            System.out.println("Deployed validator = " + rpv.getContractAddress());

            System.out.println("Calling validate(lowerBound, upperBound, commitment, proof) on validator contract.");

            boolean result = rpv.validate(lowerBound, upperBound, commitment, proof).send();

            if (result) {
                System.out.println("Proof validated successfully in HyperLedger!");
                return true;
            } else {
                System.out.println("Proof validation failed in HyperLedger");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Cannot call smart contract: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private Credentials getCredentials() {
        final String privateKey = Config.getInstance().getProperty("private.key");
        return Credentials.create(privateKey);
    }

    public String getAddress() {
        return getCredentials().getAddress();
    }

}
