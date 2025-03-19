package com.backend.model;

import com.backend.enums.ActivityPreference;
import com.backend.enums.Role;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coach {
    private String coachId;
    private String firstName;
    private String lastName;
    private String about;
    private List<String> fileUrls;
    private String email;
    private String imageUrl;
    private Role role;
    // Coach-specific attributes (optional for normal users)
    private List<ActivityPreference> specializations;
    private String motivationPitch;
    private String summary;
    private Double rating;

    @DynamoDbPartitionKey
    public String getCoachId() {
        return coachId;
    }

    @DynamoDbSecondarySortKey(indexNames = {"CoachRatingIndex"})
    public Double getRating() {
        return rating;
    }
}