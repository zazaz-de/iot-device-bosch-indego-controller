package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import org.springframework.stereotype.Service;

import de.zazaz.iot.bosch.indego.srvmock.service.AuthenticationService;

@Service
public class MockAuthenticationServiceImpl implements AuthenticationService {

    @Override
    public String authenticate (String username, String password)
    {
        return MockObjects
            .findIndegoDeviceByUsernameAndPassword(username, password)
            .map(o -> o.getUserId())
            .orElse(null);
    }

}
