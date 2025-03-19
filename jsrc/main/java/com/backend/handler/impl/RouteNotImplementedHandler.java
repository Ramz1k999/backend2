package com.backend.handler.impl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.backend.handler.EndpointHandler;
import com.google.gson.Gson;


import java.util.Map;

public class  RouteNotImplementedHandler implements EndpointHandler {

    private final Gson gson;
    public RouteNotImplementedHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(501)
                .withBody(gson.toJson(Map.of("message",
                                "Handler for the %s method on the %s path is not implemented."
                                        .formatted(requestEvent.getHttpMethod(), requestEvent.getPath())
                        ).toString())
                );
    }

}
