package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import de.zazaz.iot.bosch.indego.srvmock.service.ContextHandlerService;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

public interface MockContextHandlerService extends ContextHandlerService {

    MockIndegoDevice getDevice (String contextId, String serial) throws IndegoServiceException;

}
