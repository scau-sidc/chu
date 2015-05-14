package com.github.scausidc.chu.user.exception;

public class LoggedOutException extends RuntimeException
{
    public LoggedOutException()
    {
        super();
    }

    public LoggedOutException(String message)
    {
        super(message);
    }

    public LoggedOutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LoggedOutException(Throwable cause)
    {
        super(cause);
    }
}
