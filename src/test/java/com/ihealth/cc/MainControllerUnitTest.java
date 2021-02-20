package com.ihealth.cc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MainControllerUnitTest {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @InjectMocks
    MainController controller;

    @Mock
    AmazonClient client;

    @Mock
    public ProjectRepository projectRepository;

    @Mock
    public CoverageRepository coverageRepository;

    @Test
    void getAllProjects() {
        ProjectEntity p = new ProjectEntity();
        p.setName("project 1");
        List<ProjectEntity> projects = Arrays.asList(p);
        when(projectRepository.findAll()).thenReturn(projects);
        Iterable <ProjectEntity> projectsReturn = controller.getAllProjects();
        assertEquals(projectsReturn, projects);
    }

    @Test
    void getCommitsByProjectName() {
        CoverageEntity p = new CoverageEntity();
        p.setProjectName("project 1");
        List<CoverageEntity> c = Arrays.asList(p);
        when(coverageRepository.findByProjectName("project 1")).thenReturn(c);
        Iterable <CoverageEntity> projectsReturn = controller.getCommitsByProjectName("project 1");
        assertEquals(projectsReturn, c);
    }

    @Test
    void analyseCoverage() throws Exception {
        Path path = Paths.get("src/test/test-clover.xml");
        String cloverName = "test-clover.xml";
        String projectName = "AProjectName";
        String originalFileName = "test-clover.xml";
        String contentType = "text/xml";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            logger.info(e);
        }
        MultipartFile multipartFile = new MockMultipartFile(cloverName, originalFileName, contentType, content);

        ProjectEntity p = new ProjectEntity();
        p.setName("project 1");
        List<ProjectEntity> projects = Arrays.asList(p);
        when(projectRepository.findByName(cloverName)).thenReturn(projects);

        CoverageEntity coverageEntity = new CoverageEntity();
        coverageEntity.setProjectName("project 1");
        coverageEntity.setPercentage(0.1);
        List<CoverageEntity> comparingCommitEntity = Arrays.asList(coverageEntity);
        when(coverageRepository.findByBranchOrderByCreatedAtDesc("main")).thenReturn(comparingCommitEntity);

        String reportFolderName = "coverageReport";
        String cloudfrontDomain = "https://test.com";
        String url = cloudfrontDomain + cloverName;
        when(client.getCloudFrontLink(reportFolderName)).thenReturn(url);

        String customizedFolder = null;
        String covReturn = controller.analyseCoverage(
                reportFolderName,
                multipartFile,
                customizedFolder,
                projectName,
                "dev",
                "main",
                "asdferes",
                "push");

        //[Current coverage is 8.4%, The comparing commit coverage is 10.0%](https://test.com/clover-dev/project name/test-clover.xml).
        String coverageDirectoryReport = "### [Current coverage is 8.4%, The comparing commit coverage is 10.0%](" + url + "). ";

        assertEquals(coverageDirectoryReport, covReturn);

        // test customizedFolder is not null
        customizedFolder = "/target/site";
        String customizedUrl = cloudfrontDomain + customizedFolder;
        when(client.getCloudFrontLink(customizedFolder)).thenReturn(customizedUrl);

        covReturn = controller.analyseCoverage(
                reportFolderName,
                multipartFile,
                customizedFolder,
                projectName,
                "dev",
                "main",
                "asdferes",
                "push");

        //[Current coverage is 8.4%, The comparing commit coverage is 10.0%](https://test.com/clover-dev/project name/test-clover.xml).
        coverageDirectoryReport = "### [Current coverage is 8.4%, The comparing commit coverage is 10.0%](" + url + "). ";
        String customizedReport = System.lineSeparator() + "### [Customized report](" + customizedUrl + "). ";

        logger.info(covReturn);

        assertEquals(coverageDirectoryReport + customizedReport, covReturn);
    }
}
