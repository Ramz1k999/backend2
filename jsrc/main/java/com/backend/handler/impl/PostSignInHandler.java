package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.backend.auth.CognitoSupport;
import com.backend.dto.SignIn;
import com.backend.handler.EndpointHandler;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;

import javax.inject.Inject;

public class PostSignInHandler extends CognitoSupport implements EndpointHandler {

    @Inject
    public PostSignInHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            SignIn signIn = SignIn.fromJson(requestEvent.getBody());
            AdminInitiateAuthResponse authResponse = cognitoSignIn(signIn.email(), signIn.password());

            if (authResponse.authenticationResult() == null) {
                throw new RuntimeException("Authentication failed. No token received.");
            }

            // token
            String token = authResponse.authenticationResult().idToken();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("token", token).toString());
        } catch (UserNotConfirmedException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject()
                            .put("error", "User is not confirmed. Please check your email for verification.")
                            .toString());
        } catch (NotAuthorizedException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject()
                            .put("error", "Invalid username or password")
                            .toString());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject()
                            .put("error", e.getMessage())
                            .toString());
        }
    }
}
