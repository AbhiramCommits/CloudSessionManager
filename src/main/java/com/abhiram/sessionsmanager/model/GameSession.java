package com.abhiram.sessionsmanager.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Data
@RedisHash("sessions")
public class GameSession {

    @Id
    private String sessionId;
    private String userId;
    private String serverRegion;
    private String gameTitle;
    private Instant createdAt;
    private Instant lastActiveAt;
    private SessionStatus status;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long ttl = 30L;
}
