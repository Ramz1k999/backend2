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
                .stream()
                .map(this::mapToWorkout)
                .collect(Collectors.toList());
    }

    private Workout mapToWorkout(Workout workout) {
        return Workout.builder()
                .id(workout.getId())
                .activity(workout.getActivity())
                .clientId(workout.getClientId())
                .coachId(workout.getCoachId())
                .feedbackId(workout.getFeedbackId())
                .dateTime(workout.getDateTime())
                .description(workout.getDescription())
                .state(workout.getState())
                .build();
    }



}
