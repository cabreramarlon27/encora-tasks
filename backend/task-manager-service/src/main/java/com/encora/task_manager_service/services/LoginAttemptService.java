package com.encora.task_manager_service.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5; // Maximum allowed attempts
    private final ConcurrentHashMap<String, AtomicInteger> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        attemptsCache.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public boolean isBlocked(String key) {
        return attemptsCache.containsKey(key) && attemptsCache.get(key).get() >= MAX_ATTEMPT;
    }

    public void resetAttemptsCache(){
        attemptsCache.clear();
    }
}