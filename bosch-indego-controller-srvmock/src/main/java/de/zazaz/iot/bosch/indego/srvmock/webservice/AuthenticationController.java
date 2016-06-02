package de.zazaz.iot.bosch.indego.srvmock.webservice;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.zazaz.iot.bosch.indego.AuthenticationRequest;
import de.zazaz.iot.bosch.indego.AuthenticationResponse;
import de.zazaz.iot.bosch.indego.srvmock.service.AuthenticationService;
import de.zazaz.iot.bosch.indego.srvmock.service.ContextHandlerService;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoContext;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController extends AbstractController {
    
    @Autowired
    private AuthenticationService srvAuthentication;
    
    @Autowired
    private ContextHandlerService srvContextHandler;
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request, 
            @RequestHeader(IndegoWebConstants.HEADER_AUTHORIZATION) String authorization) 
                    throws IndegoServiceException
    {
        if ( authorization == null || !authorization.startsWith("Basic ") ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        byte[] userPassBytes = Base64.getDecoder().decode(authorization.substring(6).trim());
        String[] userPass = new String(userPassBytes).split(":");
        String username = userPass.length > 0 ? userPass[0] : "";
        String password = userPass.length > 1 ? userPass[1] : "";
        
        String userId = srvAuthentication.authenticate(username, password); 
        if ( userId == null ) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        IndegoContext context = srvContextHandler.createContextForUser(userId);
        
        AuthenticationResponse result = new AuthenticationResponse();
        result.setAlmSn(context.getDeviceSerial());
        result.setContextId(context.getContext());
        result.setUserId(context.getUserId());
        return new ResponseEntity<AuthenticationResponse>(result, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.DELETE)
    public void logout(
            @RequestHeader(IndegoWebConstants.HEADER_CONTEXT_ID) String contextId) 
                    throws IndegoServiceException 
    {
        srvContextHandler.deleteContext(contextId);
    }
    
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseEntity<Void> check (
            @RequestHeader(IndegoWebConstants.HEADER_AUTHORIZATION) String authorization)
                    throws IndegoServiceException
    {
        // TODO Implement
        throw new RuntimeException("Not implemented");
    }
    
}
