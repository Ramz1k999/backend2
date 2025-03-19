package com.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Feedback {
    private String feedbackId;
    private String workoutId;
    private String userId;
    private String coachId;
    private String comment;
    private int rating;

    @DynamoDbPartitionKey
    public String getFeedbackId() {
        return feedbackId;
    }

    @DynamoDbSortKey
    public String getWorkoutId() {
        return workoutId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"UserWorkoutFeedbackIndex"})
    public String getUserId() {
        return userId;
    }

    @DynamoDbSecondarySortKey(indexNames = {"UserWorkoutFeedbackIndex"})
    public String getWorkoutIdForIndex() {
        return workoutId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"FeedbackRatingIndex"})
    public String getCoachId() {
        return coachId;
    }

    @DynamoDbSecondarySortKey(indexNames = {"FeedbackRatingIndex"})
    public int getRating() {
        return rating;
    }
}


