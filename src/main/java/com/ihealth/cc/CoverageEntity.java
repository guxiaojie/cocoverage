package com.ihealth.cc;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "coverage", schema = "cc", catalog = "")
public class CoverageEntity {
    private int id;
    private String projectName;
    private String baseBranch;
    private String comparingBranch;
    private String baseCommitId;
    private double percentage;
    private String action;
    private Timestamp createdAt;
    private String branch;
    private String commitId;
    private String reportFile;
    private String reportDirectory;
    private String customizedDirectory;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "project_name", nullable = true, length = 255)
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Basic
    @Column(name = "base_branch", nullable = false, length = 255)
    public String getBaseBranch() {
        return baseBranch;
    }

    public void setBaseBranch(String baseBranch) {
        this.baseBranch = baseBranch;
    }

    @Basic
    @Column(name = "comparing_branch", nullable = true, length = 255)
    public String getComparingBranch() {
        return comparingBranch;
    }

    public void setComparingBranch(String comparingBranch) {
        this.comparingBranch = comparingBranch;
    }

    @Basic
    @Column(name = "base_commit_id", nullable = false, length = 255)
    public String getBaseCommitId() {
        return baseCommitId;
    }

    public void setBaseCommitId(String baseCommitId) {
        this.baseCommitId = baseCommitId;
    }

    @Basic
    @Column(name = "percentage", nullable = false, precision = 0)
    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Basic
    @Column(name = "action", nullable = false, length = 255)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Basic
    @Column(name = "created_at", nullable = true)
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Basic
    @Column(name = "branch", nullable = true, length = 255)
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Basic
    @Column(name = "commit_id", nullable = false, length = 255)
    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    @Basic
    @Column(name = "report_file", nullable = true, length = 255)
    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    @Basic
    @Column(name = "report_directory", nullable = true, length = 255)
    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    @Basic
    @Column(name = "customized_directory", nullable = true, length = 255)
    public String getCustomizedDirectory() {
        return customizedDirectory;
    }

    public void setCustomizedDirectory(String customizedDirectory) {
        this.customizedDirectory = customizedDirectory;
    }

}
