package de.zazaz.iot.bosch.indego.srvmock.service;

public class IndegoServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public IndegoServiceException ()
    {
    }

    public IndegoServiceException (String message)
    {
        super(message);
    }

    public IndegoServiceException (Throwable cause)
    {
        super(cause);
    }

    public IndegoServiceException (String message, Throwable cause)
    {
        super(message, cause);
    }

    public IndegoServiceException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
