package com.ihealth.cc;

import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import java.io.File;

@Configuration
@EnableAutoConfiguration
public class AmazonClient {

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.cloudfrontDomain}")
    private String cloudfrontDomain;
    private static final Logger logger = LogManager.getLogger(AmazonClient.class);

    @Autowired
    AmazonS3 amazonS3;

    public AmazonClient() {
        super();
    }

    @Override
    public String toString() {
        return "AmazonClient [endpointUrl=" + endpointUrl + ", bucketName=" + bucketName;
    }

    public String uploadToS3(File file, String fileName) {
        logger.info("Uploading file to S3 bucket " + bucketName);
        String fileUrl = "";
        try {
            PutObjectResult putResult = amazonS3.putObject(bucketName+"/clover-xml", fileName, file);
            logger.info("putResult is {}", putResult);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            return fileUrl;
        } catch (AmazonServiceException e) {
            logger.error(e.getErrorMessage());
            System.exit(1);
            e.printStackTrace();
        }
        return fileUrl;
    }

    public String getCloudFrontLink(String fileName) {
        String url = cloudfrontDomain + fileName;
        logger.info("url: {}", url);
        return url;
    }

}
