package com.abhiram.sessionsmanager.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SessionRoutingService {

    private static final List<String> REGIONS = List.of("us-west", "us-east", "eu-central");
    private static final long SEED = 42L;
    private final Random random = new Random(SEED);

    public String pickOptimalRegion() {
        String bestRegion = null;
        int lowestLatency = Integer.MAX_VALUE;

        for (String region : REGIONS) {
            int latency = simulateLatency(region);
            if (latency < lowestLatency) {
                lowestLatency = latency;
                bestRegion = region;
            }
        }

        return bestRegion;
    }

    private int simulateLatency(String region) {
        return 20 + random.nextInt(80);
    }
}
