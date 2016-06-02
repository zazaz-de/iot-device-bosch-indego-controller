package de.zazaz.iot.bosch.indego.srvmock.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.zazaz.iot.bosch.indego.DeviceCommand;
import de.zazaz.iot.bosch.indego.DeviceStateInformation;
import de.zazaz.iot.bosch.indego.DeviceStatus;
import de.zazaz.iot.bosch.indego.SetStateRequest;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoService;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

@RestController
@RequestMapping("alms/{alm_sn}/state")
public class StateController extends AbstractController {
    
    @Autowired
    private IndegoService srvIndego;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<DeviceStateInformation> getState(
            @PathVariable("alm_sn") String serial, 
            @RequestHeader(IndegoWebConstants.HEADER_CONTEXT_ID) String contextId) 
                    throws IndegoServiceException
    {
        return new ResponseEntity<>(srvIndego.getState(contextId, serial), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public void setState(
            @PathVariable("alm_sn") String serial, 
            @RequestHeader(IndegoWebConstants.HEADER_CONTEXT_ID) String contextId, 
            @RequestBody SetStateRequest newState) 
                    throws IndegoServiceException
    {
        DeviceStateInformation state = srvIndego.getState(contextId, serial);
        DeviceStatus status = DeviceStatus.decodeStatusCode(state.getState());
        
        if ( newState.getState().equals("mow") ) {
            if ( status.getAssociatedCommand() == DeviceCommand.MOW ) {
                throw new IndegoServiceException("Device is already mowing");
            }
            state.setState(513);
        }
        else if ( newState.getState().equals("pause") ) {
            if ( status.getAssociatedCommand() == DeviceCommand.PAUSE ) {
                throw new IndegoServiceException("Device is already paused");
            }
            if ( status.getAssociatedCommand() == DeviceCommand.RETURN ) {
                throw new IndegoServiceException("Device is in dock");
            }
            state.setState(517);
        }
        else if ( newState.getState().equals("returnToDock") ) {
            if ( status.getAssociatedCommand() == DeviceCommand.RETURN ) {
                throw new IndegoServiceException("Device is already docked");
            }
            // TODO Allowed if in pause state?
            state.setError(258);
        }
        else {
            throw new IndegoServiceException(String.format("Unknown command: %s", newState.getState()));
        }
    }
    
}
