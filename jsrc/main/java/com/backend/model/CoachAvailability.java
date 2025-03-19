package com.backend.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachAvailability {
    private String coachId;
    private String date;
    private String timeSlot; // Example: "10:00-11:00 AM"

    @DynamoDbPartitionKey
    public String getCoachId() {
        return coachId;
    }

    @DynamoDbSortKey
    public String getDate() {
        return date;
    }
}
