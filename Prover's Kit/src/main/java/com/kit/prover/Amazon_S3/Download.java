package com.kit.prover.Amazon_S3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.kit.prover.zeroknowledge.prover.Config;

import java.io.*;
import java.nio.file.Files;

public class Download {

    String bucketName = Config.getInstance().getProperty("proverBucket");
    String accessKey = Config.getInstance().getProperty("accesskey");
    String secretKey = Config.getInstance().getProperty("secretkey");
    String downloadLocation = Config.getInstance().getProperty("download.location");
    //String key = "Confirmation_letter.pdf";

    public void getTheFile(String fileName) throws IOException {
        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        String key = fileName;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_WEST_2)
                    .withCredentials(credentialsProvider)
                    .build();

            S3Object fetchFile = s3Client.getObject(new GetObjectRequest(bucketName, key));
            final BufferedInputStream i = new BufferedInputStream(fetchFile.getObjectContent());
            InputStream objectData = fetchFile.getObjectContent();
            Files.copy(objectData, new File(downloadLocation + key).toPath());
            objectData.close();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }
}
