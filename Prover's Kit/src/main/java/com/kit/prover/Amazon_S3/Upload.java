package com.kit.prover.Amazon_S3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kit.prover.zeroknowledge.prover.Config;

import java.io.File;
import java.io.IOException;

public class Upload {

    String bucketName = Config.getInstance().getProperty("verifierBucket");
    String accessKey = Config.getInstance().getProperty("accesskey");
    String secretKey = Config.getInstance().getProperty("secretkey");
    String uploadLocation = Config.getInstance().getProperty("upload.location");

    public void uploadTheFile(String fileName) throws IOException {

        String newFileName = fileName.substring(0, fileName.length()-4);

        System.out.println("The file to be uploaded is:" + newFileName);

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_WEST_2)
                    .withCredentials(credentialsProvider)
                    .build();

            String rangeProofExtension = Config.getInstance().getProperty("rangeProofExtension");
            s3Client.putObject(new PutObjectRequest(bucketName, newFileName + rangeProofExtension,
                    new File(uploadLocation + newFileName + rangeProofExtension))
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }
        catch (Exception e) {
            System.out.println("Error occurred while saving the file to S3.");
        }
    }

}
