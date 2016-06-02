package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MockObjects {
    
    public static final List<MockIndegoDevice> lstDevices = Collections.unmodifiableList(new ArrayList<MockIndegoDevice>() {
        private static final long serialVersionUID = 1L;
        {
            add(new MockIndegoDevice("user1@indego.local", "pass1", "fd5db7a0-f552-4664-bd95-11ffe36df930", "1234567890"));
            add(new MockIndegoDevice("user2@indego.local", "pass2", "69835bae-f1d5-4640-80ed-fedc1d845764", "0987654321"));
        }
    });
    
    public static Optional<MockIndegoDevice> findIndegoDeviceByUsernameAndPassword (String username, String password)
    {
        return lstDevices.stream()
                .filter(o -> o.getUsername().equalsIgnoreCase(username) && o.getPassword().equals(password))
                .findFirst();
    }
    
    public static Optional<MockIndegoDevice> findIndegoDeviceByUserId (String userId)
    {
        return lstDevices.stream()
                .filter(o -> o.getUserId().equalsIgnoreCase(userId))
                .findFirst();
    }
    
    public static Optional<MockIndegoDevice> findIndegoDeviceBySerial (String serial)
    {
        return lstDevices.stream()
                .filter(o -> serial.equalsIgnoreCase(o.getDeviceSerial()))
                .findFirst();
    }
    
    public static Optional<MockIndegoDevice> findIndegoDeviceByContext (String context)
    {
        return lstDevices.stream()
                .filter(o -> context.equalsIgnoreCase(o.getContext()))
                .findFirst();
    }
    
    public static Optional<MockIndegoDevice> findIndegoDeviceByContextAndSerial (String context, String serial)
    {
        return lstDevices.stream()
                .filter(o -> context.equalsIgnoreCase(o.getContext()) && serial.equalsIgnoreCase(o.getDeviceSerial()))
                .findFirst();
    }

}
