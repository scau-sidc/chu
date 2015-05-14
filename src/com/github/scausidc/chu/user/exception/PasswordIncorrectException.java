package com.github.scausidc.chu.user.exception;

/**
 * @deprecated currently use UnauthorizedException
 */
@Deprecated
public class PasswordIncorrectException extends RuntimeException
{
    public PasswordIncorrectException()
    {
        super();
    }

    public PasswordIncorrectException(String message)
    {
        super(message);
    }

    public PasswordIncorrectException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PasswordIncorrectException(Throwable cause)
    {
        super(cause);
    }
}
