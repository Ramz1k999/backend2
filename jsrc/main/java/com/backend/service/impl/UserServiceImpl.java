package com.backend.service.impl;

import com.backend.model.User;
import com.backend.service.UserService;
import com.backend.enums.Role;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserServiceImpl implements UserService {
    private final DynamoDbTable<User> userTable;
    private final DynamoDbIndex<User> emailIndex;

    @Inject
    public UserServiceImpl(DynamoDbClient dynamoDbClient) {
        String tableName = System.getenv("USER_TABLE");
        if (tableName == null || tableName.isEmpty()) {
            throw new RuntimeException("USER_TABLE environment variable is not set.");
        }

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.userTable = enhancedClient.table(tableName, TableSchema.fromBean(User.class));
        this.emailIndex = userTable.index("EmailIndex");
    }

    @Override
    public User getUserById(String userId) {
        return userTable.getItem(r -> r.key(k -> k.partitionValue(userId)));
    }

    @Override
    public void saveUser(User user) {
        try {
            userTable.putItem(user);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user to DynamoDB: " + e.getMessage(), e);
        }
    }

}
