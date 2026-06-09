package com.abhiram.sessionsmanager.service;

import com.abhiram.sessionsmanager.exception.SessionNotFoundException;
import com.abhiram.sessionsmanager.model.GameSession;
import com.abhiram.sessionsmanager.model.SessionRepository;
import com.abhiram.sessionsmanager.model.SessionStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public GameSession createSession(String userId, String serverRegion, String gameTitle) {
        GameSession session = new GameSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setServerRegion(serverRegion);
        session.setGameTitle(gameTitle);
        session.setCreatedAt(Instant.now());
        session.setLastActiveAt(Instant.now());
        session.setStatus(SessionStatus.ACTIVE);
        return sessionRepository.save(session);
    }

    public GameSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    public GameSession updateActivity(String sessionId) {
        GameSession session = getSession(sessionId);
        session.setLastActiveAt(Instant.now());
        session.setStatus(SessionStatus.ACTIVE);
        return sessionRepository.save(session);
    }

    public void terminateSession(String sessionId) {
        GameSession session = getSession(sessionId);
        session.setStatus(SessionStatus.TERMINATED);
        sessionRepository.save(session);
        sessionRepository.delete(session);
    }
}
