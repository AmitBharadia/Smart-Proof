package com.kit.verifier.Amazon_S3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kit.verifier.zeroknowledge.prover.Config;

import java.io.File;
import java.io.IOException;

public class Upload {

    String clientRegion = "US West (Oregon)";
    String bucketName = "cmpe272proverbucket";
    String accessKey = Config.getInstance().getProperty("accesskey");
    String secretKey = Config.getInstance().getProperty("secretkey");

    public void uploadTheFile() throws IOException {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_WEST_2)
                    .withCredentials(credentialsProvider)
                    .build();

            String fileName = Config.getInstance().getProperty("ttpmessage.file.name");
            s3Client.putObject(new PutObjectRequest(bucketName, "Shubham_Sawant_Proof.data",
                    new File(fileName))
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }
        catch (Exception e) {
            System.out.println("Error occurred while saving the file to S3.");
        }
    }

}
