package de.zazaz.iot.bosch.indego.srvmock.service.mockimpl;

import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.DeviceStateInformation.GetStateResponseRuntime;
import de.zazaz.iot.bosch.indego.DeviceStateInformation.GetStateResponseRuntimes;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoContext;

public class MockIndegoDevice implements IndegoContext {
    
    private final String username;
    
    private final String password;
    
    private final String userId;
    
    private final String deviceSerial;
    
    private DeviceStateInformation state;
    
    private String context;

    public MockIndegoDevice (String username, String password, String userId, String deviceSerial)
    {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.deviceSerial = deviceSerial;
        
        GetStateResponseRuntime sessionRuntime = new GetStateResponseRuntime();
        sessionRuntime.setCharge(10 * 1000);
        sessionRuntime.setOperate(20 * 1000);
        
        GetStateResponseRuntime totalRuntime = new GetStateResponseRuntime();
        totalRuntime.setCharge(150 * 1000);
        totalRuntime.setOperate(250 * 1000);
        
        GetStateResponseRuntimes runtime = new GetStateResponseRuntimes();
        runtime.setSession(sessionRuntime);
        runtime.setTotal(totalRuntime);
        
        state = new DeviceStateInformation();
        state.setError(0);
        state.setMapSvgCacheTimestamp(System.currentTimeMillis());
        state.setMapUpdateAvailable(false);
        state.setMowed(10);
        state.setMowedTimestamp(System.currentTimeMillis());
        state.setState(258);
        state.setRuntime(runtime);
    }

    public String getUsername ()
    {
        return username;
    }

    public String getPassword ()
    {
        return password;
    }

    @Override
    public String getUserId ()
    {
        return userId;
    }

    @Override
    public String getDeviceSerial ()
    {
        return deviceSerial;
    }
    
    @Override
    public String getContext ()
    {
        return context;
    }
    
    public void setContext (String context)
    {
        this.context = context;
    }

    public DeviceStateInformation getState ()
    {
        return state;
    }

}
