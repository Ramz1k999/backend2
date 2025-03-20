package com.backend.service.impl;

import com.backend.model.Workout;
import com.backend.service.WorkoutService;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class WorkoutServiceImpl implements WorkoutService {
    private final DynamoDbTable<Workout> workoutTable;

    @Inject
    public WorkoutServiceImpl(DynamoDbClient dynamoDbClient) {
        String tableName = System.getenv("WORKOUT_TABLE");
        if (tableName == null || tableName.isEmpty()) {
            throw new RuntimeException("WORKOUT_TABLE environment variable is not set.");
        }

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.workoutTable = enhancedClient.table(tableName, TableSchema.fromBean(Workout.class));
    }

    @Override
    public List<Workout> getWorkoutsByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build());

        return workoutTable.query(r -> r.queryConditional(queryConditional))
                .items()
                .collect(Collectors.toList());
    }

    @Override
    public List<Workout> getAvailableWorkouts() {
        return workoutTable.scan().items().stream()
                .filter(workout -> workout.getState() == WorkoutState.AVAILABLE)
                .collect(Collectors.toList());
    }

    public Workout bookWorkout(BookNewWorkoutRequestBody request) {
        // Query workout based on coachId, date, and timeSlot
        List<Workout> workouts = workoutTable.scan().items()
                .stream()
                .filter(w -> w.getCoachId().equals(request.getCoachId()) &&
                             w.getDate().equals(request.getDate()) &&
                             w.getTimeSlot().equals(request.getTimeSlot()))
                .toList();

        if (workouts.isEmpty()) {
            return null; // No matching workout found
        }

        Workout workoutToBook = workouts.get(0);

        // If the workout is already booked, return null
        if ("BOOKED".equals(workoutToBook.getState())) {
            return null;
        }

        // Update workout details
        workoutToBook.setClientId(request.getClientId());
        workoutToBook.setState("BOOKED");

        // Save updated workout to DynamoDB
        workoutTable.putItem(workoutToBook);

        return workoutToBook;
    }
}
