package de.zazaz.iot.bosch.indego.srvmock.service;

public interface ContextHandlerService {
    
    IndegoContext createContextForUser (String userId) throws IndegoServiceException;
    
    void deleteContext (String contextId) throws IndegoServiceException;

}
