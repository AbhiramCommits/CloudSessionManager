package com.abhiram.sessionsmanager.service;

import com.abhiram.sessionsmanager.exception.SessionNotFoundException;
import com.abhiram.sessionsmanager.model.GameSession;
import com.abhiram.sessionsmanager.model.SessionRepository;
import com.abhiram.sessionsmanager.model.SessionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
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
        GameSession saved = sessionRepository.save(session);
        log.info("event=session_persisted sessionId={} userId={} region={} ttl={}min",
                saved.getSessionId(), userId, serverRegion, saved.getTtl());
        return saved;
    }

    public GameSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));
    }

    public GameSession updateActivity(String sessionId) {
        GameSession session = getSession(sessionId);
        session.setLastActiveAt(Instant.now());
        session.setStatus(SessionStatus.ACTIVE);
        GameSession updated = sessionRepository.save(session);
        log.info("event=session_activity_refreshed sessionId={} lastActiveAt={}",
                sessionId, updated.getLastActiveAt());
        return updated;
    }

    public void terminateSession(String sessionId) {
        GameSession session = getSession(sessionId);
        session.setStatus(SessionStatus.TERMINATED);
        sessionRepository.save(session);
        sessionRepository.delete(session);
        log.info("event=session_deleted sessionId={} userId={} status=TERMINATED",
                sessionId, session.getUserId());
    }
}
