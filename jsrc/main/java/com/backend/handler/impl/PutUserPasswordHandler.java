package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.dto.ChangePasswordRequestBody;
import com.backend.auth.CognitoSupport;
import com.backend.handler.EndpointHandler;
import com.backend.model.User;
import com.backend.service.UserService;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.inject.Inject;

public class PutUserPasswordHandler extends CognitoSupport implements EndpointHandler {

    private final UserService userService;

    @Inject
    public PutUserPasswordHandler(CognitoIdentityProviderClient cognitoClient, UserService userService) {
        super(cognitoClient);
        this.userService = userService;
    }
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            String userId = requestEvent.getPathParameters().get("userId");
            if (userId == null || userId.isBlank()) {
                return createErrorResponse(400, "Missing userId in path parameters");
            }
            ChangePasswordRequestBody request = ChangePasswordRequestBody.fromJson(requestEvent.getBody());

            if (!userId.equals(request.userId())) {
                return createErrorResponse(400, "User ID in path and request body do not match");
            }
            User user = userService.getUserById(userId);
            if (user == null) {
                return createErrorResponse(404, "User not found");
            }

            String username = user.getEmail();

            changeUserPassword(username, request.oldPassword(), request.newPassword());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("message", "Password changed successfully")
                            .toString());

        } catch (NotAuthorizedException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(403)
                    .withBody(new JSONObject()
                            .put("message", "Invalid authentication")
                            .toString());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(new JSONObject()
                            .put("message", e.getMessage())
                            .toString());
        }
    }
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(new JSONObject()
                        .put("message", message)
                        .toString());
    }
}
