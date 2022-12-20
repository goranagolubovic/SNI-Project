package com.sni.dms.exceptions;

import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpException
{
    public NotFoundException()
    {
        super(String.valueOf(HttpStatus.CONFLICT));
    }


    public NotFoundException(String message)
    {
        super(message);
    }

}