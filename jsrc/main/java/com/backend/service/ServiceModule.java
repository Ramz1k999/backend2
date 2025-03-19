package com.backend.service;

import com.backend.service.UserService;
import com.backend.service.impl.UserServiceImpl;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

import javax.inject.Singleton;

/**
 * Module that provides services for the Dagger dependency injection.
 */
@Module
public class ServiceModule {

    @Provides
    @Singleton
    DynamoDbClient provideDynamoDbClient() {
        String region = System.getenv("REGION");
        if (region == null || region.isEmpty()) {
            throw new IllegalStateException("REGION environment variable is not set.");
        }

        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Provides
    @Singleton
    UserService provideUserService(DynamoDbClient dynamoDbClient) {
        return new UserServiceImpl(dynamoDbClient);
    }
}
