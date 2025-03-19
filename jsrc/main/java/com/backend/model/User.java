package com.backend.model;

import com.backend.enums.ActivityPreference;
import com.backend.enums.FitnessTarget;
import com.backend.enums.Role;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    //user specific
    private ActivityPreference preferableActivity;
    private Role role;
    private FitnessTarget target;



    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "EmailIndex")
    public String getEmail() {
        return email;
    }

}


