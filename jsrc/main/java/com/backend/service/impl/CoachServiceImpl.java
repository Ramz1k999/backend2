package com.backend.service.impl;

import com.backend.model.Coach;
import com.backend.service.CoachService;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CoachServiceImpl implements CoachService {
    private final DynamoDbTable<Coach> coachTable;

    @Inject
    public CoachServiceImpl(DynamoDbClient dynamoDbClient) {
        String userTableName = System.getenv("USER_TABLES");
        String coachTableName = System.getenv("COACH_TABLES");

        if (userTableName == null || userTableName.isEmpty()) {
            throw new RuntimeException("USER_TABLES environment variable is not set.");
        }

        if (coachTableName == null || coachTableName.isEmpty()) {
            throw new RuntimeException("COACH_TABLES environment variable is not set.");
        }

        // Enhanced DynamoDB client
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.coachTable = enhancedClient.table(coachTableName, TableSchema.fromBean(Coach.class));
    }

    @Override
    public boolean isAuthorizedCoach(String email) {
       return false;
    }

    @Override
    public Coach getCoachById(String coachId) {
        return coachTable.getItem(r -> r.key(k -> k.partitionValue(coachId)));
    }

}