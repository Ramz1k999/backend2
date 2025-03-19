package com.backend;

import com.backend.handler.EndpointHandler;
import com.backend.handler.HandlersModule;
import com.backend.service.ServiceModule;
import com.backend.utils.UtilsModule;
import dagger.Component;


import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Application component. Assembly of all modules for the Dagger dependency injection framework.
 * Provides the general API handler and CORS headers.
 */


@Singleton
@Component(modules = {HandlersModule.class, ServiceModule.class ,UtilsModule.class})
public interface Application {



    @Named("general")
    EndpointHandler getGeneralApiHandler();

    @Named("cors")
    Map<String, String> getCorsHeaders();


}