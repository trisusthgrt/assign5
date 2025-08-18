package com.example.ledgerly.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HealthController
 */
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBasicHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Ledgerly application is running successfully"))
                .andExpect(jsonPath("$.application").value("Ledgerly"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDetailedHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/health/detailed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("Ledgerly - Small Business Ledger & Finance Manager"))
                .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.system").exists())
                .andExpect(jsonPath("$.system.java_version").exists())
                .andExpect(jsonPath("$.system.os_name").exists())
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.web_server").value("UP"))
                .andExpect(jsonPath("$.components.application_context").value("UP"));
    }
}
