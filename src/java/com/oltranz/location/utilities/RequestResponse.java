/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.location.utilities;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Hp
 */
public class RequestResponse {
    public static final Response success( String outPut){
        return Response.ok(outPut, MediaType.APPLICATION_JSON).build();
    }
    public static final Response faillure(Status statusCode, String cause){
        return Response.status(statusCode).entity("Error: "+cause).build();
    }
}
