package com.abhiram.sessionsmanager.controller;

import com.abhiram.sessionsmanager.model.GameSession;
import com.abhiram.sessionsmanager.model.SessionCreateRequest;
import com.abhiram.sessionsmanager.service.SessionRoutingService;
import com.abhiram.sessionsmanager.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionRoutingService sessionRoutingService;

    public SessionController(SessionService sessionService, SessionRoutingService sessionRoutingService) {
        this.sessionService = sessionService;
        this.sessionRoutingService = sessionRoutingService;
    }

    @PostMapping
    public ResponseEntity<GameSession> createSession(@RequestBody SessionCreateRequest request,
                                                      Authentication authentication) {
        String userId = authentication.getName();
        String serverRegion = sessionRoutingService.pickOptimalRegion();

        GameSession session = sessionService.createSession(userId, serverRegion, request.getGameTitle());
        log.info("event=session_created sessionId={} userId={} region={} gameTitle={}",
                session.getSessionId(), userId, serverRegion, request.getGameTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<GameSession> getSession(@PathVariable String sessionId) {
        GameSession session = sessionService.getSession(sessionId);
        log.info("event=session_fetched sessionId={} userId={}", sessionId, session.getUserId());
        return ResponseEntity.ok(session);
    }

    @PatchMapping("/{sessionId}/activity")
    public ResponseEntity<GameSession> updateActivity(@PathVariable String sessionId) {
        GameSession session = sessionService.updateActivity(sessionId);
        log.info("event=session_activity_updated sessionId={} userId={}", sessionId, session.getUserId());
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> terminateSession(@PathVariable String sessionId) {
        String userId = sessionService.getSession(sessionId).getUserId();
        sessionService.terminateSession(sessionId);
        log.info("event=session_terminated sessionId={} userId={}", sessionId, userId);
        return ResponseEntity.noContent().build();
    }
}
