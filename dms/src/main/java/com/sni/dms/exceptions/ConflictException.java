package com.sni.dms.exceptions;

import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;

public class ConflictException extends HttpException
{
    public ConflictException()
    {
        super(String.valueOf(HttpStatus.CONFLICT));
    }


    public ConflictException(String message)
    {
        super(message);
    }

}
