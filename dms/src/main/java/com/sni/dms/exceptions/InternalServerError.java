package com.sni.dms.exceptions;

import org.apache.http.HttpException;
import org.springframework.http.HttpStatus;

public class InternalServerError extends HttpException
{
    public InternalServerError()
    {
        super(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
    }


    public InternalServerError(String message)
    {
        super(message);
    }

}