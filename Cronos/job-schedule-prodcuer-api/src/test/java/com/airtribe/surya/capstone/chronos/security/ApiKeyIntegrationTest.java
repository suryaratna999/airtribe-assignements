package com.airtribe.surya.capstone.chronos.security;

import com.airtribe.surya.capstone.chronos.config.SecurityConfig;
import com.airtribe.surya.capstone.chronos.controller.JobController;
import com.airtribe.surya.capstone.chronos.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JobController.class)
@Import({SecurityConfig.class, ApiKeyIntegrationTest.TestConfig.class})
@TestPropertySource(properties = {"api.key=test-integration-key", "api.key.header=X-API-KEY"})
public class ApiKeyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig {
        @Bean
        public JobService jobService() {
            // Create a simple JobService instance with a real ObjectMapper and no repository.
            // Override methods used by the controller to avoid DB calls.
            return new JobService(null, new ObjectMapper()) {
                @Override
                public List<com.airtribe.surya.capstone.chronos.entity.JobInstance> getAllJobs() {
                    return Collections.emptyList();
                }

                @Override
                public com.airtribe.surya.capstone.chronos.entity.JobInstance scheduleJob(com.airtribe.surya.capstone.chronos.dto.JobRequest request) {
                    throw new UnsupportedOperationException("Not needed for test");
                }

                @Override
                public com.airtribe.surya.capstone.chronos.entity.JobInstance getJobById(java.util.UUID jobId) {
                    throw new UnsupportedOperationException("Not needed for test");
                }

                @Override
                public void deleteJob(java.util.UUID jobId) {
                    throw new UnsupportedOperationException("Not needed for test");
                }
            };
        }
    }

    @Test
    void whenNoKey_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidKey_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/jobs").header("X-API-KEY", "bad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenValidKey_thenOk() throws Exception {
        mockMvc.perform(get("/api/v1/jobs").header("X-API-KEY", "test-integration-key"))
                .andExpect(status().isOk());
    }
}
