package com.ihealth.cc;

import com.ihealth.cc.exception.NotFoundException;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping(path="/cc")
public class MainController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CoverageRepository coverageRepository;

    @Autowired
    private AmazonClient client;

    private static final Logger logger = LogManager.getLogger(MainController.class);

    @GetMapping(path="/allprojects")
    public @ResponseBody Iterable<ProjectEntity> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping(path="/commits")
    public @ResponseBody Iterable<CoverageEntity> getCommitsByProjectName(@RequestParam String projectName) {
         return coverageRepository.findByProjectName(projectName);
    }

    @PostMapping(path="/upload")
    public @ResponseBody String analyseCoverage(@RequestParam(required = false) String reportFolderName,
                                                @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                                                @RequestParam(required = false) String customizedFolder,
                                                @RequestParam String projectName,
                                                @RequestParam String baseBranch,
                                                @RequestParam String comparingBranch,
                                                @RequestParam String baseCommitId,
                                                @RequestParam String action) throws InterruptedException {
        if (multipartFile == null) {
            throw new NotFoundException("File is not found");
        }

        // creat a file
        String fileName = multipartFile.getOriginalFilename();

        String fileKind = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        if (fileKind.compareToIgnoreCase(".xml") != 0) {
            throw new NotFoundException("XML File is not found");
        }

        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf("."), fileName.length());
        File f = new File(fileName);
        try (InputStream in = multipartFile.getInputStream(); OutputStream os = new FileOutputStream(f)) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer, 0, 4096)) != -1) {
                os.write(buffer, 0, n);
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse xml
        File file = new File(f.toURI());
        String originName = multipartFile.getOriginalFilename();
        float percentage = XMLParser.parser(originName, file);

        // upload file to s3
        client.uploadToS3(file, fileName);

        // delete local file
        if (file.delete()) {
            logger.info("delete local file successfully");
        } else {
            logger.info("fail to delete file locally");
        }

        // get the comparing commit percent; ATTENTION: get first, insert data after.
        // in case comparingBranch == baseBranch
        double comparingPercentage = 0;
        List<CoverageEntity> comparingEntity = coverageRepository.findByBranchOrderByCreatedAtDesc(comparingBranch);
        if (comparingEntity.size() > 0) {
            comparingPercentage = comparingEntity.get(0).getPercentage();
        }
        String cloudFrontLink = client.getCloudFrontLink(reportFolderName);

        //customizedFolder is not null, the user may want to upload a site
        String customizedFolderReportText = "";
        String customizedLink = "";
        if (customizedFolder != null && customizedFolder.length() > 0) {
            customizedLink = client.getCloudFrontLink(customizedFolder);
            customizedFolderReportText = formateSiteReportText(customizedLink);
        }
        saveToDB(projectName, baseBranch, comparingBranch,
                baseCommitId, action, fileName,
                percentage, cloudFrontLink, customizedLink);
        String coverageReportText = formateReportText(baseBranch, comparingBranch, percentage, comparingPercentage, cloudFrontLink);

        return coverageReportText + customizedFolderReportText;
    }

    public String formateReportText(String baseBranch,
                                    String comparingBranch,
                                    float percentage,
                                    double comparingPercentage,
                                    String cloudFrontLink) {
        DecimalFormat df = new DecimalFormat("0.0%");
        String comparingPercentageStr = "";
        if (comparingPercentage > 0) {
            comparingPercentageStr = ", The comparing commit coverage is " + df.format(comparingPercentage);
        }
        String coverageString = "Current coverage is " + df.format(percentage);

        String coverageDirectoryReport = "### [" + coverageString + comparingPercentageStr + "](" + cloudFrontLink + ")" + ". ";

        logger.info("Base branch is {}, comparing branch is {}", baseBranch, comparingBranch);
        logger.info(coverageDirectoryReport);

        return coverageDirectoryReport;
    }

    public String formateSiteReportText(String cloudFrontLink) {
        String documentation = System.lineSeparator() + "### [Customized report](" + cloudFrontLink + "). ";
        logger.info("Customized report is {}", documentation);
        return documentation;
    }

    public void saveToDB(String projectName,
                         String baseBranch,
                         String comparingBranch,
                         String baseCommitId,
                         String action,
                         @Nullable String fileName,
                         @Nullable double percentage,
                         @Nullable String cloudFrontLink,
                         @Nullable String customizedDirectory) {
        CoverageEntity c = new CoverageEntity();
        c.setProjectName(projectName);
        c.setBaseBranch(baseBranch);
        c.setComparingBranch(comparingBranch);
        c.setBaseCommitId(baseCommitId);
        c.setBranch(baseBranch);
        c.setCommitId(baseCommitId);
        //the xml file(clover.xml)
        c.setReportFile(fileName);
        c.setPercentage(percentage);
        c.setAction(action);
        Timestamp t = new Timestamp(new Date().getTime());
        c.setCreatedAt(t);
        c.setReportDirectory(cloudFrontLink);
        c.setCustomizedDirectory(customizedDirectory);
        coverageRepository.save(c);

        ProjectEntity n = new ProjectEntity();
        if (projectRepository.findByName(projectName).size() == 0 ) {
            n.setName(projectName);
            n.setCreatedAt(t);
            projectRepository.save(n);
        }
    }

}
