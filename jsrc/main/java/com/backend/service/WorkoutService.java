package com.backend.service;

import com.backend.model.Workout;
import java.util.List;

public interface WorkoutService {
    List<Workout> getWorkoutsByUserId(String userId);
}
