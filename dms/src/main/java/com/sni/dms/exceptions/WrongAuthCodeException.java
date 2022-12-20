package com.sni.dms.exceptions;


public class WrongAuthCodeException extends Exception
{
    public WrongAuthCodeException()
    {
        super();
    }


    public WrongAuthCodeException(String message)
    {
        super(message);
    }

}