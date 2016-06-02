package de.zazaz.iot.bosch.indego.srvmock.webservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.zazaz.iot.bosch.indego.srvmock.service.IndegoSecurityException;
import de.zazaz.iot.bosch.indego.srvmock.service.IndegoServiceException;

public abstract class AbstractController {

    @ExceptionHandler(IndegoSecurityException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public void handleIndegoSecurityException ()
    {
    }

    @ExceptionHandler(IndegoServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleIndegoServiceException ()
    {
    }

}
