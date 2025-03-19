package com.backend.service;


import com.backend.model.Coach;

public interface CoachService {
    boolean isAuthorizedCoach(String email);
    Coach getCoachById(String userId);
}