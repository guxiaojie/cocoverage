package com.ihealth.cc;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CoverageRepository extends CrudRepository<CoverageEntity, Integer> {

    public List<CoverageEntity> findByBranchOrderByCreatedAtDesc(String branch);
    public List<CoverageEntity> findByProjectName(String projectName);
    public List<CoverageEntity> findByCommitId(String commitId);

}
