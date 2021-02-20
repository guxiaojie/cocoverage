package com.ihealth.cc;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<ProjectEntity, Integer> {

    public List<ProjectEntity> findByName(String name);

}
