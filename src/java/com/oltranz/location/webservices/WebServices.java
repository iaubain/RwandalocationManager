/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.location.webservices;

import com.oltranz.location.config.AppDesc;
import com.oltranz.location.logic.AppReceiver;
import static java.lang.System.out;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Path("/Rwanda")
@Stateless
public class WebServices {
    @EJB
            AppReceiver receiver;
    @GET
    @Path("/locations")
    public Response getAllTickets(@Context HttpServletRequest requestContext, @Context HttpHeaders headers, String body){
        out.print(AppDesc.APP_DESC+" Source IP: " + requestContext.getRemoteAddr() + ", Port: " + requestContext.getRemotePort() + ", Host: " + requestContext.getRemoteHost());
        out.print(AppDesc.APP_DESC+" Node Received Headers "+headers.toString()+" and Body  "+body);
        return receiver.receiver(requestContext.getRemoteAddr(), requestContext.getRemotePort(), headers, body);//.clientReceiver(headers, body);
    }
}