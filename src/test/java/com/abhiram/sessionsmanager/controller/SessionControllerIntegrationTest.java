package com.abhiram.sessionsmanager.controller;

import com.abhiram.sessionsmanager.model.GameSession;
import com.abhiram.sessionsmanager.model.SessionStatus;
import com.abhiram.sessionsmanager.service.JwtService;
import com.abhiram.sessionsmanager.service.SessionRoutingService;
import com.abhiram.sessionsmanager.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionRoutingService sessionRoutingService;

    @Test
    void createSessionWithoutTokenReturns403() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("gameTitle", "Fortnite"));

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSessionWithValidTokenReturns201AndSessionId() throws Exception {
        String token = jwtService.generateToken("user-1");
        GameSession mockSession = new GameSession();
        mockSession.setSessionId("mock-session-id-123");
        mockSession.setUserId("user-1");
        mockSession.setServerRegion("us-west");
        mockSession.setGameTitle("Fortnite");
        mockSession.setCreatedAt(Instant.now());
        mockSession.setLastActiveAt(Instant.now());
        mockSession.setStatus(SessionStatus.ACTIVE);

        when(sessionRoutingService.pickOptimalRegion()).thenReturn("us-west");
        when(sessionService.createSession(anyString(), anyString(), anyString()))
                .thenReturn(mockSession);

        String body = objectMapper.writeValueAsString(Map.of("gameTitle", "Fortnite"));

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value("mock-session-id-123"))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.serverRegion").value("us-west"));
    }
}
