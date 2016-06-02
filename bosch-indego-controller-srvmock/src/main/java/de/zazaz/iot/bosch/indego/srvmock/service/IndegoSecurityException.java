package de.zazaz.iot.bosch.indego.srvmock.service;

public class IndegoSecurityException extends IndegoServiceException {

    private static final long serialVersionUID = 1L;

    public IndegoSecurityException ()
    {
    }

    public IndegoSecurityException (String message)
    {
        super(message);
    }

    public IndegoSecurityException (Throwable cause)
    {
        super(cause);
    }

    public IndegoSecurityException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public IndegoSecurityException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
