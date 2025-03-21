package com.backend.service;

import com.backend.model.Workout;
import java.util.List;

public interface WorkoutService {
    List<Workout> getWorkoutsByUserId(String userId);
    List<Workout> getAvailableWorkouts();
    Workout bookWorkout(BookNewWorkoutRequestBody request);
    BookedWorkoutListResponse cancelWorkout(String workoutId);

}
