package com.ihealth.cc;

 import com.amazonaws.services.s3.AmazonS3;
 import com.amazonaws.services.s3.model.PutObjectResult;
 import org.junit.jupiter.api.AfterEach;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
 import org.springframework.test.context.TestPropertySource;
 import org.springframework.test.util.ReflectionTestUtils;

 import java.io.File;

 import static org.junit.jupiter.api.Assertions.*;

 import static org.mockito.Mockito.when;
@TestPropertySource(properties = {
        "amazonProperties.endpointUrl=https://s3.us-west-2.amazonaws.com",
        "amazonProperties.bucketName=file.coveragecollector",
        "amazonProperties.cloudfrontDomain=https://d3cz3j55k0iap3.cloudfront.net"
})
@SpringBootTest
@AutoConfigureMockMvc
public class AmazonClientTest {

    @InjectMocks
    private AmazonClient client;

    @Mock
    private AmazonS3 amazonS3;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.cloudfrontDomain}")
    private String cloudfrontDomain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testToString() {
        assertEquals("AmazonClient [endpointUrl=null, bucketName=null", client.toString());
    }

    @Test
    public void uploadToS3() throws Exception {
        String bucketName = "bucket";
        String fileName = "fileName";
        File file = new File("example_clover.xml");

        PutObjectResult s3_url = new PutObjectResult();
        when(amazonS3.putObject(bucketName, fileName, file)).thenReturn(s3_url);

        ReflectionTestUtils.setField(client, "endpointUrl", endpointUrl);
        ReflectionTestUtils.setField(client, "bucketName", bucketName);

        String retunrUrl = client.uploadToS3(file, fileName);
        assertEquals(endpointUrl + "/" + bucketName + "/" + fileName, retunrUrl);
    }

    @Configuration
    static class Config {

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
            return new PropertySourcesPlaceholderConfigurer();
        }

    }

    @Test
    public void getCloudFrontLink() throws Exception {
        String fileName = "clover1608024339539.xml";
        String projectName = "coverage";
        ReflectionTestUtils.setField(client, "cloudfrontDomain", cloudfrontDomain);

        String retunrUrl = client.getCloudFrontLink(fileName);
        assertEquals(cloudfrontDomain +  fileName, retunrUrl);
    }

}
