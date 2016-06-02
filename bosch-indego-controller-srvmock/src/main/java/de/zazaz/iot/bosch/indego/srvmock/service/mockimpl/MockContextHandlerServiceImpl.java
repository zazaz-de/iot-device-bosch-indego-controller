package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.zazaz.iot.bosch.indego.srvmock.service.IndegoContext;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoSecurityException;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

@Service
public class MockContextHandlerServiceImpl implements MockContextHandlerService {

    @Override
    public IndegoContext createContextForUser (String userId) throws IndegoServiceException
    {
        MockIndegoDevice device = MockObjects
            .findIndegoDeviceByUserId(userId)
            .orElseThrow(() -> new IndegoServiceException(String.format("Invalid user id: %s", userId)));

        device.setContext(UUID.randomUUID().toString());
        return device;
    }

    @Override
    public void deleteContext (String contextId) throws IndegoServiceException
    {
        MockObjects
            .findIndegoDeviceByContext(contextId)
            .orElseThrow(() -> new IndegoServiceException(String.format("Invalid context id: %s", contextId)))
            .setContext(null);
    }

    @Override
    public MockIndegoDevice getDevice (String contextId, String serial) throws IndegoServiceException
    {
        return MockObjects
            .findIndegoDeviceByContextAndSerial(contextId, serial)
            .orElseThrow(() -> new IndegoSecurityException(
                    String.format("Invalid context id and serial: %s / %s", contextId, serial)));
    }

}
