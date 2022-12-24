package com.sni.dms.exceptions;

public class ForbiddenAccessFromIpAddress extends Exception
{
    public ForbiddenAccessFromIpAddress()
    {
        super();
    }


    public ForbiddenAccessFromIpAddress(String message)
    {
        super(message);
    }

}