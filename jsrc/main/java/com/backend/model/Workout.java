package com.backend.model;

import com.backend.enums.*;
import lombok.Builder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;


@Builder
@DynamoDbBean
public class Workout {
    private String id;
    private ActivityPreference activity;
    private String clientId;
    private String coachId;
    private String dateTime;
    private String description;
    private String feedbackId;
    private WorkoutState state;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("activity")
    @DynamoDbSecondaryPartitionKey(indexNames = {"workout-search-index"})
    public ActivityPreference getActivity() {
        return activity;
    }
    public void setActivity(ActivityPreference activity) {
        this.activity = activity;
    }

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    @DynamoDbAttribute("coachId")
    @DynamoDbSecondaryPartitionKey(indexNames = {"coach-date-index"})
    public String getCoachId() {
        return coachId;
    }
    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    @DynamoDbAttribute("dateTime")
    @DynamoDbSecondarySortKey(indexNames = {"booked-workouts-index", "coach-date-index", "workout-search-index"})
    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    @DynamoDbAttribute("state")
    @DynamoDbSecondaryPartitionKey(indexNames = {"booked-workouts-index"})
    public WorkoutState getState() {
        return state;
    }
    public void setState(WorkoutState state) {
        this.state = state;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"feedback-index"})
    public String getFeedbackId() {
        return feedbackId;
    }

}

