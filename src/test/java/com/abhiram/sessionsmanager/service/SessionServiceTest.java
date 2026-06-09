package com.abhiram.sessionsmanager.service;

import com.abhiram.sessionsmanager.model.GameSession;
import com.abhiram.sessionsmanager.model.SessionRepository;
import com.abhiram.sessionsmanager.model.SessionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void createSessionSavesToRepositoryAndReturnsCorrectFields() {
        when(sessionRepository.save(any(GameSession.class))).thenAnswer(inv -> inv.getArgument(0));

        GameSession result = sessionService.createSession("user-1", "us-west", "Fortnite");

        assertThat(result.getSessionId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user-1");
        assertThat(result.getServerRegion()).isEqualTo("us-west");
        assertThat(result.getGameTitle()).isEqualTo("Fortnite");
        assertThat(result.getStatus()).isEqualTo(SessionStatus.ACTIVE);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getLastActiveAt()).isNotNull();
        assertThat(result.getTtl()).isEqualTo(30L);

        verify(sessionRepository).save(any(GameSession.class));
    }

    @Test
    void updateActivityRefreshesLastActiveAt() {
        GameSession existing = new GameSession();
        existing.setSessionId("sess-1");
        existing.setUserId("user-1");
        existing.setLastActiveAt(Instant.now().minusSeconds(600));
        existing.setStatus(SessionStatus.IDLE);
        Instant oldLastActiveAt = existing.getLastActiveAt();

        when(sessionRepository.findById("sess-1")).thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(GameSession.class))).thenAnswer(inv -> inv.getArgument(0));

        GameSession result = sessionService.updateActivity("sess-1");

        assertThat(result.getLastActiveAt()).isAfter(oldLastActiveAt);
        assertThat(result.getStatus()).isEqualTo(SessionStatus.ACTIVE);

        verify(sessionRepository).save(existing);
    }

    @Test
    void terminateSessionDeletesFromRedis() {
        GameSession existing = new GameSession();
        existing.setSessionId("sess-1");
        existing.setUserId("user-1");
        existing.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findById("sess-1")).thenReturn(Optional.of(existing));
        when(sessionRepository.save(any(GameSession.class))).thenReturn(existing);

        sessionService.terminateSession("sess-1");

        assertThat(existing.getStatus()).isEqualTo(SessionStatus.TERMINATED);

        ArgumentCaptor<GameSession> captor = ArgumentCaptor.forClass(GameSession.class);
        verify(sessionRepository).save(captor.capture());
        verify(sessionRepository).delete(captor.getValue());
    }
}
