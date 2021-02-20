package com.ihealth.cc;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RequestTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    public void getAllProjects() throws Exception {
        ProjectEntity jsonObj = new ProjectEntity();
        jsonObj.setName("name");
        Gson gson = new Gson();
        String  json = gson.toJson(jsonObj);

        ProjectEntity p = new ProjectEntity();
        p.setName("Jason");
        p.setId(1);
        List<ProjectEntity> projects = Arrays.asList(p);

        when(projectRepository.findAll()).thenReturn(projects);

        mvc.perform(MockMvcRequestBuilders.get("/cc/allprojects")
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jason"));
    }
}
