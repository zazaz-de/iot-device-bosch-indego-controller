package de.zazaz.iot.bosch.indego.srvmock.service;

import de.zazaz.iot.bosch.indego.DeviceStateInformation;

public interface IndegoService {

    DeviceStateInformation getState (String contextId, String serial) throws IndegoServiceException;

}
