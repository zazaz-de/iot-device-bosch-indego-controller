package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoService;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

@Service
public class MockIndegoServiceImpl implements IndegoService {
    
    @Autowired
    private MockContextHandlerService srvMockContextHandler;
    
    @Override
    public DeviceStateInformation getState (String contextId, String serial) throws IndegoServiceException
    {
        MockIndegoDevice device = srvMockContextHandler.getDevice(contextId, serial);
        return device.getState();
    }

}
